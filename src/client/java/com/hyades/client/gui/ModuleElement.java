package com.hyades.client.gui;

import com.hyades.client.module.Module;
import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.RenderUtil;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import com.hyades.client.util.animation.SmoothAnimationTimer;
import com.hyades.client.util.math.Easings;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * 模块行：显示模块名 + 键位 + 展开箭头，点击切换开关。
 */
public class ModuleElement extends UIElement {

    public static final float ROW_HEIGHT = 16.0f;
    public static final float PADDING_X = 6.0f;
    private static final float CHEVRON_WIDTH = 14.0f;

    private final Module module;
    private final SmoothAnimationTimer enableAnimation = new SmoothAnimationTimer();
    private boolean expanded;

    public ModuleElement(Module module, float x, float y, float width) {
        super(x, y, width, ROW_HEIGHT);
        this.module = module;
        if (module.isEnabled()) {
            this.enableAnimation.setValue(1.0);
        }
    }

    public Module getModule() {
        return this.module;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        this.enableAnimation.update();
        this.enableAnimation.animate(this.module.isEnabled() ? 1.0 : 0.0, 0.18, Easings.EASE_OUT_QUAD);

        FontRenderer font = FontPresets.axiformaBold(9.5f);
        FontRenderer dimFont = FontPresets.defaultFont(8.5f);

        // 背景：启用时从左侧填充主题色，否则轻微 hover 高亮
        float progress = (float) this.enableAnimation.getValueF();
        if (progress > 0.01f) {
            ctx.fillRect(this.x, this.y, this.width * progress, this.height, 0x2D58A6FF);
        }
        if (this.hovered) {
            ctx.fillRect(this.x, this.y, this.width, this.height, Theme.HOVER_OVERLAY);
        }
        // 左侧指示条
        if (progress > 0.01f) {
            ctx.fillRect(this.x, this.y, 1.5f, this.height, Theme.PRIMARY);
        }

        ctx.drawString(this.module.getName(), this.x + PADDING_X + 3.0f,
                this.y + (this.height - font.getHeight()) / 2.0f - 1.0f,
                font, new Paint().setColor(progress > 0.5f ? 0xFFFFFFFF : Theme.TEXT));

        // 键位 + 展开箭头（仅在模块有设置时显示箭头）
        String keyBind = this.module.getKeyBindName();
        if (keyBind != null && !"None".equals(keyBind)) {
            float kbWidth = dimFont.getStringWidth(keyBind);
            ctx.drawString(keyBind, this.x + this.width - CHEVRON_WIDTH - PADDING_X - kbWidth,
                    this.y + (this.height - dimFont.getHeight()) / 2.0f - 1.0f,
                    dimFont, new Paint().setColor(Theme.TEXT_DIM));
        }
        if (this.module.hasSettings()) {
            String chevron = this.expanded ? "\u25BC" : "\u25B6";
            float chevronWidth = dimFont.getStringWidth(chevron);
            ctx.drawString(chevron, this.x + this.width - CHEVRON_WIDTH + (CHEVRON_WIDTH - chevronWidth) / 2.0f,
                    this.y + (this.height - dimFont.getHeight()) / 2.0f - 1.0f,
                    dimFont, new Paint().setColor(Theme.TEXT_DIM));
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (!this.hovered || event.button() != 0) {
            return false;
        }
        if (this.module.hasSettings() && event.x() >= this.x + this.width - CHEVRON_WIDTH) {
            this.expanded = !this.expanded;
            return true;
        }
        this.module.toggle();
        return true;
    }
}