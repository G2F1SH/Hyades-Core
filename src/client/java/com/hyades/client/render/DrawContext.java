package com.hyades.client.render;

import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.util.ColorUtil;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

/**
 * 绘制上下文（类 Android Canvas）。
 * <p>
 * 封装 {@link GuiGraphicsExtractor} 与变换矩阵，提供面向对象的 2D 绘制原语：
 * 基础形状、圆角矩形（CPU 分段 + 覆盖率抗锯齿）、线条、圆弧、渐变、文本、纹理与裁剪。
 * 变换通过 {@link Matrix3x2fStack} 累积，绘制时被逐元素快照。
 */
public class DrawContext {

    private final GuiGraphicsExtractor graphics;
    private final Deque<Rectangle> clipStack = new ArrayDeque<>();

    public DrawContext(GuiGraphicsExtractor graphics) {
        this.graphics = graphics;
    }

    // ------------------------------------------------------------------ access

    public GuiGraphicsExtractor graphics() {
        return this.graphics;
    }

    public Matrix3x2fStack pose() {
        return this.graphics.pose();
    }

    public int guiWidth() {
        return this.graphics.guiWidth();
    }

    public int guiHeight() {
        return this.graphics.guiHeight();
    }

    // -------------------------------------------------------------- transforms

    public void translate(float x, float y) {
        this.graphics.pose().translate(x, y);
    }

    public void scale(float sx, float sy) {
        this.graphics.pose().scale(sx, sy);
    }

    public void scale(float s) {
        this.graphics.pose().scale(s);
    }

    /** 以原点为中心旋转（角度制） */
    public void rotate(float degrees) {
        this.graphics.pose().rotate((float) Math.toRadians(degrees));
    }

    /** 围绕给定中心点旋转（角度制） */
    public void rotateAround(float degrees, float cx, float cy) {
        this.graphics.pose().rotateAbout((float) Math.toRadians(degrees), cx, cy);
    }

    public void save() {
        this.graphics.pose().pushMatrix();
    }

    public void restore() {
        this.graphics.pose().popMatrix();
    }

    // ------------------------------------------------------------------- fill

    public void fillRect(float x, float y, float width, float height, int color) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }
        this.graphics.fill(Math.round(x), Math.round(y), Math.round(x + width), Math.round(y + height), color);
    }

    public void drawRect(Rectangle rectangle, int color) {
        this.fillRect(rectangle.x1(), rectangle.y1(), rectangle.getWidth(), rectangle.getHeight(), color);
    }

    /** 垂直渐变（topColor → bottomColor） */
    public void drawGradientVertical(float x, float y, float width, float height, int topColor, int bottomColor) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }
        this.graphics.fillGradient(Math.round(x), Math.round(y), Math.round(x + width), Math.round(y + height), topColor, bottomColor);
    }

    /** 水平渐变（leftColor → rightColor），以条带近似 */
    public void drawGradientHorizontal(float x, float y, float width, float height, int leftColor, int rightColor) {
        int slices = Math.max(1, Math.min(64, (int) Math.ceil(width / 2.0f)));
        float sliceWidth = width / slices;
        for (int i = 0; i < slices; i++) {
            float t = (i + 0.5f) / slices;
            int color = ColorUtil.lerpColor(leftColor, rightColor, t);
            this.fillRect(x + i * sliceWidth, y, sliceWidth + 0.5f, height, color);
        }
    }

    /** 描边矩形 */
    public void outlineRect(float x, float y, float width, float height, float strokeWidth, int color) {
        float half = strokeWidth / 2.0f;
        this.fillRect(x - half, y - half, width + strokeWidth, strokeWidth, color);
        this.fillRect(x - half, y + height - half, width + strokeWidth, strokeWidth, color);
        this.fillRect(x - half, y, strokeWidth, height, color);
        this.fillRect(x + width - half, y, strokeWidth, height, color);
    }

    /**
     * 圆角矩形（CPU 分段 + 覆盖率 AA）。
     *
     * @param radius 统一圆角半径
     */
    public void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        this.drawRoundedRect(x, y, width, height, radius, radius, radius, radius, color);
    }

    public void drawRoundedRect(RoundedRectangle roundedRectangle, int color) {
        this.drawRoundedRect(roundedRectangle.x(), roundedRectangle.y(), roundedRectangle.width(), roundedRectangle.height(),
                roundedRectangle.radiusTopLeft(), roundedRectangle.radiusTopRight(), roundedRectangle.radiusBottomRight(),
                roundedRectangle.radiusBottomLeft(), color);
    }

    /**
     * 四个角各自独立半径的圆角矩形。
     * <p>
     * 当前 CPU 实现以四角中的最小半径绘制（保证几何正确且无空洞），
     * 独立半径的 GPU SDF 版本见 {@code render/shader/RoundedRectShader}。
     */
    public void drawRoundedRect(float x, float y, float width, float height,
                                float rTL, float rTR, float rBR, float rBL, int color) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }
        float maxR = Math.min(width, height) / 2.0f;
        float r = clampRadius(Math.min(Math.min(rTL, rTR), Math.min(rBR, rBL)), maxR);
        this.drawRoundedRect(x, y, width, height, r, color);
    }

    /** 带线性渐变的圆角矩形（沿渐变方向逐切片取色） */
    public void drawRoundedRectGradient(float x, float y, float width, float height, float radius, LinearGradient gradient) {
        float maxR = Math.min(width, height) / 2.0f;
        radius = clampRadius(radius, maxR);
        if (radius < 1.0f) {
            this.drawGradient(x, y, width, height, gradient);
            return;
        }

        float innerW = width - 2.0f * radius;
        float innerH = height - 2.0f * radius;
        // 中央主体（按渐变方向分段取色）
        int slices = Math.max(1, Math.min(48, (int) Math.ceil(innerW / 4.0f)));
        float sliceW = innerW / slices;
        for (int i = 0; i < slices; i++) {
            float cx = x + radius + i * sliceW + sliceW / 2.0f;
            float t = (cx - x) / width;
            this.fillRect(x + radius + i * sliceW, y, sliceW + 0.5f, height, gradient.sample(t));
        }
        // 左右直带
        int bandSlices = Math.max(1, Math.min(16, (int) Math.ceil(innerH / 4.0f)));
        float bandH = innerH / bandSlices;
        for (int i = 0; i < bandSlices; i++) {
            float cy = y + radius + i * bandH + bandH / 2.0f;
            float t = (cy - y) / height;
            int color = gradient.sample(t);
            this.fillRect(x, y + radius + i * bandH, radius, bandH + 0.5f, color);
            this.fillRect(x + width - radius, y + radius + i * bandH, radius, bandH + 0.5f, color);
        }
        fillCorner(x, y, radius, true, false, gradient.sample(0.0f));
        fillCorner(x + width - radius, y, radius, true, true, gradient.sample(width > 0 ? radius / width : 0.0f));
        fillCorner(x, y + height - radius, radius, false, false, gradient.sample(height > 0 ? (height - radius) / height : 0.0f));
        fillCorner(x + width - radius, y + height - radius, radius, false, true, gradient.sample(1.0f));
    }

    /** 沿渐变主方向绘制矩形（角度 0°=垂直，90°=水平） */
    public void drawGradient(float x, float y, float width, float height, LinearGradient gradient) {
        float angle = gradient.getAngleDeg();
        boolean vertical = Math.abs(Math.round(angle / 90.0f) % 2) == 0;
        if (vertical) {
            this.drawGradientVertical(x, y, width, height, gradient.getColorA(), gradient.getColorB());
        } else {
            this.drawGradientHorizontal(x, y, width, height, gradient.getColorA(), gradient.getColorB());
        }
    }

    // ------------------------------------------------------------------- line

    public void drawLine(float x1, float y1, float x2, float y2, float strokeWidth, int color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist <= 0.0) {
            return;
        }
        float half = strokeWidth / 2.0f;
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        this.save();
        this.translate(x1, y1);
        this.rotate(angle);
        this.fillRect(-half, -half, (float) dist + strokeWidth, strokeWidth, color);
        if (this.strokeCapState == Paint.StrokeCap.ROUND) {
            this.drawCircleFilled(x1, y1, half, color);
            this.drawCircleFilled(x2, y2, half, color);
        }
        this.restore();
    }

    private Paint.StrokeCap strokeCapState = Paint.StrokeCap.ROUND;

    /** 设置后续线条的端帽（供 {@link #drawLine} 使用） */
    public void setStrokeCapState(Paint.StrokeCap cap) {
        this.strokeCapState = cap == null ? Paint.StrokeCap.ROUND : cap;
    }

    // ------------------------------------------------------------------- arc

    /** 圆弧（角度制，顺时针），线段近似 */
    public void drawArc(float cx, float cy, float radius, float startDeg, float endDeg, float strokeWidth, int color) {
        if (radius <= 0.0f || strokeWidth <= 0.0f) {
            return;
        }
        float sweep = endDeg - startDeg;
        int segments = Math.max(2, (int) Math.ceil(Math.abs(sweep) / 5.0f));
        float prevX = cx + radius * (float) Math.cos(Math.toRadians(startDeg));
        float prevY = cy + radius * (float) Math.sin(Math.toRadians(startDeg));
        for (int i = 1; i <= segments; i++) {
            float angle = startDeg + sweep * i / segments;
            float px = cx + radius * (float) Math.cos(Math.toRadians(angle));
            float py = cy + radius * (float) Math.sin(Math.toRadians(angle));
            this.drawLine(prevX, prevY, px, py, strokeWidth, color);
            prevX = px;
            prevY = py;
        }
    }

    // ----------------------------------------------------------------- circle

    public void drawCircleFilled(float cx, float cy, float radius, int color) {
        if (radius <= 0.0f) {
            return;
        }
        int r = Math.max(1, (int) Math.ceil(radius));
        for (int px = -r; px <= r; px++) {
            float xs = px + 0.5f;
            float halfH = (float) Math.sqrt(Math.max(0.0, radius * radius - xs * xs));
            int fullHeight = (int) Math.floor(halfH * 2.0f);
            if (fullHeight > 0) {
                this.fillRect(cx + px, cy - fullHeight / 2.0f, 1.0f, fullHeight, color);
            }
            float fraction = halfH * 2.0f - fullHeight;
            if (fraction > 0.01f) {
                int edge = ColorUtil.withAlpha(color, fraction);
                this.fillRect(cx + px, cy - fullHeight / 2.0f - 1, 1.0f, 1.0f, edge);
                this.fillRect(cx + px, cy + fullHeight / 2.0f, 1.0f, 1.0f, edge);
            }
        }
    }

    // ------------------------------------------------------------------- text

    public void drawString(String text, float x, float y, FontRenderer fontRenderer, Paint paint) {
        fontRenderer.drawString(this, text, x, y, paint);
    }

    public float getStringWidth(String text, FontRenderer fontRenderer) {
        return fontRenderer.getStringWidth(text);
    }

    // --------------------------------------------------------------- textures

    public void drawTexture(Identifier texture, float x, float y, float width, float height) {
        int w = Math.round(width);
        int h = Math.round(height);
        this.graphics.blit(texture, Math.round(x), Math.round(y), Math.round(x) + w, Math.round(y) + h,
                0.0f, 1.0f, 0.0f, 1.0f);
    }

    public void drawTexture(GpuTextureView textureView, GpuSampler sampler,
                            float x, float y, float width, float height,
                            float u0, float u1, float v0, float v1, int color) {
        this.graphics.blit(textureView, sampler,
                Math.round(x), Math.round(y), Math.round(x + width), Math.round(y + height),
                u0, u1, v0, v1);
    }

    /** 从纹理管理器注册的纹理中绘制子区域（支持颜色着色），用于字形图集等 */
    public void drawTexturedSubRect(Identifier texture, float x, float y, float width, float height,
                                    float u, float v, int textureWidth, int textureHeight, int color) {
        this.graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture,
                Math.round(x), Math.round(y), u, v, Math.round(width), Math.round(height),
                textureWidth, textureHeight, color);
    }

    // -------------------------------------------------------------------- clip

    public void clip(float x, float y, float width, float height) {
        Rectangle rect = Rectangle.ofXYWH(x, y, width, height);
        this.clipStack.push(rect);
        this.graphics.enableScissor(Math.round(rect.x1()), Math.round(rect.y1()), Math.round(rect.x2()), Math.round(rect.y2()));
    }

    public void clipRect(Rectangle rectangle) {
        this.clip(rectangle.x1(), rectangle.y1(), rectangle.getWidth(), rectangle.getHeight());
    }

    /** 弹出最近一次裁剪，恢复上一个裁剪状态（如果没有更上层则关闭 scissor） */
    public void popClip() {
        this.clipStack.pop();
        if (this.clipStack.isEmpty()) {
            this.graphics.disableScissor();
        } else {
            Rectangle rect = this.clipStack.peek();
            this.graphics.enableScissor(Math.round(rect.x1()), Math.round(rect.y1()), Math.round(rect.x2()), Math.round(rect.y2()));
        }
    }

    public void clearClipStack() {
        while (!this.clipStack.isEmpty()) {
            this.graphics.disableScissor();
            this.clipStack.pop();
        }
    }

    // -------------------------------------------------------------------- blur

    /**
     * 背景模糊绘制的降级实现：绘制内容后叠加半透明深色底。
     * <p>
     * MC 26.2 渲染状态提取管线中暂未接入自定义 FBO 模糊，这里以半透明底近似，
     * 保证 API 兼容且不崩溃（优雅降级）。
     */
    public void drawBlur(float x, float y, float width, float height, float radius, Runnable content) {
        if (content != null) {
            content.run();
        }
        this.fillRect(x, y, width, height, ColorUtil.withAlpha(0x1F1F1F, 0.45f));
        float border = Math.max(1.0f, radius);
        this.outlineRect(x, y, width, height, border, ColorUtil.withAlpha(0x161616, 0.35f));
    }

    // --------------------------------------------------------------- internals

    /**
     * 填充单个圆角角块。角块矩形左上角为 (cx, cy)，半径为 radius。
     * <p>
     * 以角块自身左上角为原点建立局部坐标，圆弧圆心位于 (r, r)、半径 r，
     * 逐像素列计算圆弧切点并对边界像素做覆盖率抗锯齿。
     *
     * @param flipX 是否沿水平翻转（右上 / 右下角块）
     */
    private void fillCorner(float cx, float cy, float radius, boolean top, boolean flipX, int color) {
        if (radius <= 0.5f) {
            return;
        }
        int r = (int) Math.ceil(radius);
        for (int px = 0; px <= r; px++) {
            // u：从外侧边缘（0）到内侧边缘（r）的局部坐标
            float u = flipX ? (radius - px) : px;
            float xs = u + 0.5f;
            float dsq = radius * radius - (xs - radius) * (xs - radius);
            if (dsq < 0.0f) {
                continue;
            }
            float depth = (float) Math.sqrt(dsq);
            float edge = radius - depth;

            if (top) {
                int start = (int) Math.ceil(edge);
                int height = r - start;
                if (height > 0) {
                    this.fillRect(cx + px, cy + start, 1.0f, height, color);
                }
                if (edge > 0.0f) {
                    float coverage = 1.0f - (edge - (float) Math.floor(edge));
                    if (coverage > 0.01f && start > 0) {
                        int edgeColor = ColorUtil.withAlpha(color, coverage);
                        this.fillRect(cx + px, cy + start - 1, 1.0f, 1.0f, edgeColor);
                    }
                }
            } else {
                float filled = radius - edge;
                int full = (int) Math.floor(filled);
                if (full > 0) {
                    this.fillRect(cx + px, cy, 1.0f, full, color);
                }
                float coverage = filled - full;
                if (coverage > 0.01f && full < r) {
                    int edgeColor = ColorUtil.withAlpha(color, coverage);
                    this.fillRect(cx + px, cy + full, 1.0f, 1.0f, edgeColor);
                }
            }
        }
    }

    private static float clampRadius(float radius, float max) {
        return Math.max(0.0f, Math.min(radius, max));
    }
}