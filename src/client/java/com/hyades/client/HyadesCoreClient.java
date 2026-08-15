package com.hyades.client;

import com.hyades.client.api.HyadesCoreAPI;
import com.hyades.client.api.HyadesCoreClientExtension;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 客户端入口：加载外部 {@link HyadesCoreClientExtension}，由消费方模组自行注册模块与 HUD。
 */
public class HyadesCoreClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		loadExtensions();
	}

	/** 加载其他模组注册的 {@link HyadesCoreClientExtension} 入口点 */
	private void loadExtensions() {
		for (HyadesCoreClientExtension extension : FabricLoader.getInstance()
				.getEntrypoints("hyades-core-client", HyadesCoreClientExtension.class)) {
			extension.onHyadesCoreInitialize(HyadesCoreAPI.INSTANCE);
		}
	}
}
