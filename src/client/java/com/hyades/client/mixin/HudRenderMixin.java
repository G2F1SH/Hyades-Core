package com.hyades.client.mixin;

import com.hyades.client.hud.HudRenderer;
import com.hyades.client.render.DrawContext;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 HUD 渲染末尾注入自定义 HUD 元素。
 */
@Mixin(Hud.class)
public class HudRenderMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void hyades$renderCustomHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        HudRenderer.INSTANCE.render(new DrawContext(graphics), deltaTracker);
    }
}