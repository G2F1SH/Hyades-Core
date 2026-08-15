package com.hyades.client.gui;

import com.hyades.client.module.Category;
import com.hyades.client.module.Module;
import com.hyades.client.module.ModuleManager;
import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import com.hyades.client.setting.BooleanSetting;
import com.hyades.client.setting.ModeSetting;
import com.hyades.client.setting.MultiSelectSetting;
import com.hyades.client.setting.NumberSetting;
import com.hyades.client.setting.Setting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * ClickGUI 的分类面板：可拖动、含分类页签、模块列表与设置展开区。
 */
public class CategoryPanel extends UIElement {

    public static final float WIDTH = 210.0f;
    private static final float HEADER_HEIGHT = 26.0f;
    private static final float TAB_HEIGHT = 20.0f;
    private static final float PADDING = 6.0f;
    private static final int VISIBLE_ROWS = 6;

    private final ClickGuiScreen screen;
    private Category selectedCategory = Category.COMBAT;

    private final List<ModuleElement> moduleElements = new ArrayList<>();
    private final List<SettingElement> activeSettings = new ArrayList<>();
    private final Set<Module> expandedModules = new HashSet<>();

    private float scroll;
    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;
    private float contentTop;

    public CategoryPanel(float x, float y, ClickGuiScreen screen) {
        super(x, y, WIDTH, HEADER_HEIGHT + TAB_HEIGHT + PADDING * 2 + VISIBLE_ROWS * ModuleElement.ROW_HEIGHT);
        this.screen = screen;
        this.rebuildRows();
    }

    private void rebuildRows() {
        this.moduleElements.clear();
        for (Module module : ModuleManager.INSTANCE.getModulesByCategory(this.selectedCategory)) {
            ModuleElement element = new ModuleElement(module, this.x, 0, this.width);
            element.setExpanded(this.expandedModules.contains(module));
            this.moduleElements.add(element);
        }
        this.layoutRows();
        this.refreshSettings();
    }

    private void layoutRows() {
        float top = this.y + HEADER_HEIGHT + TAB_HEIGHT + PADDING;
        float contentHeight = this.height - HEADER_HEIGHT - TAB_HEIGHT - PADDING * 2;
        this.contentTop = top;
        for (int i = 0; i < this.moduleElements.size(); i++) {
            ModuleElement element = this.moduleElements.get(i);
            element.setPosition(this.x + PADDING, top + PADDING + i * ModuleElement.ROW_HEIGHT - this.scroll);
        }
    }

    private void refreshSettings() {
        this.activeSettings.clear();
        for (ModuleElement element : this.moduleElements) {
            if (!element.isExpanded()) {
                continue;
            }
            Module module = element.getModule();
            float sx = this.x + PADDING + 10.0f;
            float sy = element.getY() + ModuleElement.ROW_HEIGHT;
            for (Setting<?> setting : module.getSettings()) {
                SettingElement row = this.createSettingElement(setting, sx, sy, this.width - PADDING * 2 - 10.0f);
                this.activeSettings.add(row);
                sy += SettingElement.ROW_HEIGHT;
            }
        }
    }

    private SettingElement createSettingElement(Setting<?> setting, float x, float y, float width) {
        return switch (setting.getType()) {
            case BOOLEAN -> new BooleanSettingElement((BooleanSetting) setting, x, y, width);
            case MODE -> new ModeSettingElement((ModeSetting) setting, x, y, width);
            case NUMBER -> new NumberSettingElement((NumberSetting) setting, x, y, width);
            case MULTI_SELECT -> new MultiSelectSettingElement((MultiSelectSetting) setting, x, y, width);
        };
    }

    public void selectCategory(Category category) {
        if (this.selectedCategory != category) {
            this.selectedCategory = category;
            this.scroll = 0.0f;
            this.rebuildRows();
        }
    }

    // ---------------------------------------------------------------- rendering

    @Override
    protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        this.layoutRows();
        this.refreshSettings();

        float contentHeight = this.height - HEADER_HEIGHT - TAB_HEIGHT - PADDING * 2;
        float maxScroll = Math.max(0.0f, this.moduleElements.size() * ModuleElement.ROW_HEIGHT - contentHeight);
        this.scroll = Math.max(0.0f, Math.min(this.scroll, maxScroll));

        // 面板主体
        ctx.drawRoundedRect(this.x, this.y, this.width, this.height, 6.0f, Theme.PANEL);
        ctx.outlineRect(this.x, this.y, this.width, this.height, 1.0f, Theme.BORDER);

        // 头部
        ctx.drawRoundedRectGradient(this.x, this.y, this.width, HEADER_HEIGHT, 6.0f,
                new com.hyades.client.render.LinearGradient(Theme.GRADIENT_TOP, Theme.GRADIENT_BOTTOM, 90.0f));
        FontRenderer headerFont = FontPresets.axiformaBold(12.0f);
        ctx.drawString("HYADES", this.x + PADDING + 2.0f, this.y + (HEADER_HEIGHT - headerFont.getHeight()) / 2.0f - 1.0f,
                headerFont, new Paint().setColor(0xFFFFFFFF));
        FontRenderer dimFont = FontPresets.defaultFont(9.0f);
        String categoryLabel = this.selectedCategory.getDisplayName().toUpperCase();
        ctx.drawString(categoryLabel, this.x + this.width - dimFont.getStringWidth(categoryLabel) - PADDING - 2.0f,
                this.y + (HEADER_HEIGHT - dimFont.getHeight()) / 2.0f - 1.0f, dimFont, new Paint().setColor(0x88FFFFFF));

        // 分类页签
        float tabY = this.y + HEADER_HEIGHT;
        float tabX = this.x + PADDING;
        float tabWidth = (this.width - PADDING * 2) / Category.values().length;
        for (Category category : Category.values()) {
            boolean selected = category == this.selectedCategory;
            int textColor = selected ? Theme.TEXT : Theme.TEXT_DIM;
            ctx.drawString(category.getDisplayName(), tabX + tabWidth / 2.0f - dimFont.getStringWidth(category.getDisplayName()) / 2.0f,
                    tabY + (TAB_HEIGHT - dimFont.getHeight()) / 2.0f - 1.0f, dimFont, new Paint().setColor(textColor));
            if (selected) {
                ctx.fillRect(tabX + 4.0f, tabY + TAB_HEIGHT - 2.0f, tabWidth - 8.0f, 2.0f, Theme.PRIMARY);
            }
            tabX += tabWidth;
        }

        // 模块列表（裁剪）
        ctx.clip(this.x + PADDING, this.y + HEADER_HEIGHT + TAB_HEIGHT, this.width - PADDING * 2, contentHeight);
        for (ModuleElement element : this.moduleElements) {
            element.render(ctx, mouseX, mouseY, deltaTicks);
        }
        // 展开模块的设置
        for (SettingElement settingElement : this.activeSettings) {
            settingElement.render(ctx, mouseX, mouseY, deltaTicks);
        }
        ctx.popClip();
    }

    // ------------------------------------------------------------------ events

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        float mx = (float) event.x();
        float my = (float) event.y();

        // 头部：关闭按钮 / 拖动
        if (my >= this.y && my <= this.y + HEADER_HEIGHT) {
            float closeRight = this.x + this.width - 4.0f;
            if (mx >= closeRight - 14.0f && mx <= closeRight) {
                this.screen.close();
                return true;
            }
            if (event.button() == 0) {
                this.dragging = true;
                this.dragOffsetX = mx - this.x;
                this.dragOffsetY = my - this.y;
                return true;
            }
        }

        // 页签
        if (my >= this.y + HEADER_HEIGHT && my <= this.y + HEADER_HEIGHT + TAB_HEIGHT) {
            if (event.button() == 0) {
                float tabWidth = (this.width - PADDING * 2) / Category.values().length;
                int index = (int) ((mx - this.x - PADDING) / tabWidth);
                if (index >= 0 && index < Category.values().length) {
                    this.selectCategory(Category.values()[index]);
                    return true;
                }
            }
        }

        // 模块行与设置
        if (my >= this.contentTop) {
            for (ModuleElement element : this.moduleElements) {
                if (element.contains(mx, my) && element.mouseClicked(event, doubleClicked)) {
                    this.refreshSettings();
                    return true;
                }
            }
            for (SettingElement settingElement : this.activeSettings) {
                if (settingElement.contains(mx, my) && settingElement.mouseClicked(event, doubleClicked)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.dragging = false;
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (this.dragging) {
            float newX = (float) event.x() - this.dragOffsetX;
            float newY = (float) event.y() - this.dragOffsetY;
            newX = Math.max(0.0f, Math.min(newX, 1920.0f - this.width));
            newY = Math.max(0.0f, Math.min(newY, 1080.0f - this.height));
            this.setPosition(newX, newY);
            return true;
        }
        return false;
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        float contentHeight = this.height - HEADER_HEIGHT - TAB_HEIGHT - PADDING * 2;
        float maxScroll = Math.max(0.0f, this.moduleElements.size() * ModuleElement.ROW_HEIGHT - contentHeight);
        this.scroll -= (float) vertical * 12.0f;
        this.scroll = Math.max(0.0f, Math.min(this.scroll, maxScroll));
    }
}