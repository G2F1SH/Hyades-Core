package com.hyades.client;

import com.hyades.HyadesCore;
import com.hyades.client.api.HyadesCoreAPI;
import com.hyades.client.api.HyadesCoreClientExtension;
import com.hyades.client.gui.ClickGuiScreen;
import com.hyades.client.hud.HudRenderer;
import com.hyades.client.hud.HudElementModule;
import com.hyades.client.hud.KeyBindsHud;
import com.hyades.client.hud.ModuleListHud;
import com.hyades.client.hud.TargetHud;
import com.hyades.client.hud.WatermarkHud;
import com.hyades.client.module.ModuleManager;
import com.hyades.client.module.impl.FullbrightModule;
import com.hyades.client.module.impl.NoFallModule;
import com.hyades.client.module.impl.SpeedModule;
import com.hyades.client.module.impl.SprintModule;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * 客户端入口：注册模块、HUD 元素与全局键位，并加载外部 {@link HyadesCoreClientExtension}。
 */
public class HyadesCoreClient implements ClientModInitializer {

	private KeyMapping clickGuiKey;
	private KeyMapping hudEditKey;

	@Override
	public void onInitializeClient() {
		registerModules();
		registerHudElements();
		registerKeyMappings();
		loadExtensions();
	}

	private void registerModules() {
		ModuleManager moduleManager = ModuleManager.INSTANCE;
		moduleManager.register(new SprintModule());
		moduleManager.register(new SpeedModule());
		moduleManager.register(new NoFallModule());
		moduleManager.register(new FullbrightModule());
	}

	private void registerHudElements() {
		HudRenderer hud = HudRenderer.INSTANCE;
		hud.register(new HudElementModule(new WatermarkHud(), "Watermark"));
		hud.register(new HudElementModule(new ModuleListHud(), "ModuleList"));
		hud.register(new HudElementModule(new KeyBindsHud(), "KeyBinds"));
		hud.register(new HudElementModule(new TargetHud(), "Target"));
		// 默认启用水印与模块列表
		for (HudElementModule element : hud.getElements()) {
			String id = element.getHudElement().getId();
			if ("Watermark".equals(id) || "ModuleList".equals(id)) {
				element.enable();
			}
		}
	}

	private void registerKeyMappings() {
		// ClickGUI 键位
		this.clickGuiKey = new KeyMapping(
				"key.hyades.clickgui",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_SHIFT,
				Category.MISC);
		KeyMappingHelper.registerKeyMapping(this.clickGuiKey);

		// HUD 编辑模式键位
		this.hudEditKey = new KeyMapping(
				"key.hyades.hudedit",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_END,
				Category.MISC);
		KeyMappingHelper.registerKeyMapping(this.hudEditKey);

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
	}

	private void onClientTick(Minecraft client) {
		while (this.clickGuiKey.consumeClick()) {
			client.gui.setScreen(new ClickGuiScreen());
		}
		while (this.hudEditKey.consumeClick()) {
			HudRenderer.INSTANCE.toggleDragMode();
		}
		ModuleManager.INSTANCE.updateKeyStates();
	}

	/** 加载其他模组注册的 {@link HyadesCoreClientExtension} 入口点 */
	private void loadExtensions() {
		for (HyadesCoreClientExtension extension : FabricLoader.getInstance()
				.getEntrypoints("hyades-core-client", HyadesCoreClientExtension.class)) {
			extension.onHyadesCoreInitialize(HyadesCoreAPI.INSTANCE);
		}
	}
}