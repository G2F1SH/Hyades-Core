package com.hyades.client.hud;

import com.hyades.client.gui.Theme;
import com.hyades.client.module.Module;
import com.hyades.client.module.ModuleManager;
import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import com.hyades.client.util.ColorUtil;
import com.hyades.client.util.animation.SmoothAnimationTimer;
import com.hyades.client.util.math.Easings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

/**
 * 右上角模块列表：按启用模块名逐行渲染，带滑入动画与彩虹点缀。
 */
public class ModuleListHud extends HudElement {

    private static final float ROW_HEIGHT = 10.0f;
    private static final float PADDING = 5.0f;

    private final Map<String, SmoothAnimationTimer> entryTimers = new HashMap<>();

    public ModuleListHud() {
        super("ModuleList", -1, 4);
    }

    private float getRowWidth() {
        float max = 0.0f;
        FontRenderer font = FontPresets.defaultFont(9.0f);
        List<Module> enabled = this.getEnabledModules();
        for (Module module : enabled) {
            max = Math.max(max, font.getStringWidth(module.getName()));
        }
        return max;
    }

    private List<Module> getEnabledModules() {
        return ModuleManager.INSTANCE.getModules().stream()
                .filter(m -> m.isEnabled() && m.getCategory() != com.hyades.client.module.Category.HUD)
                .toList();
    }

    @Override
    public float getWidth() {
        return this.getRowWidth() + PADDING * 2;
    }

    @Override
    public float getHeight() {
        return Math.max(0, this.getEnabledModules().size()) * ROW_HEIGHT + PADDING;
    }

    @Override
    protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!this.isMoved()) {
            this.x = minecraft.getWindow().getGuiScaledWidth() - this.getWidth() - 4.0f;
        }
        List<Module> enabled = this.getEnabledModules();
        if (enabled.isEmpty()) {
            return;
        }

        FontRenderer font = FontPresets.defaultFont(9.0f);
        float width = this.getWidth();
        float height = enabled.size() * ROW_HEIGHT + PADDING;
        ctx.drawRoundedRect(this.x, this.y, width, height, 5.0f, 0xB40B0E12);
        ctx.outlineRect(this.x, this.y, width, height, 1.0f, Theme.BORDER);

        float rowY = this.y + PADDING / 2.0f;
        for (int i = enabled.size() - 1; i >= 0; i--) {
            Module module = enabled.get(i);
            SmoothAnimationTimer timer = this.entryTimers.computeIfAbsent(module.getName(), n -> new SmoothAnimationTimer());
            timer.update();
            timer.animate(1.0, 0.25, Easings.EASE_OUT_CUBIC);
            float t = (float) timer.getValueF();
            if (t <= 0.01f) {
                rowY += ROW_HEIGHT;
                continue;
            }
            float offset = (1.0f - t) * 24.0f;
            String name = module.getName();
            float textWidth = font.getStringWidth(name);
            int color = ColorUtil.getRainbowColor(0.25f, i * 0.05f, 0.6f, 1.0f);
            ctx.drawString(name, this.x + width - textWidth + offset, rowY + 1.0f, font, new Paint().setColor(color));
            rowY += ROW_HEIGHT;
        }
    }
}