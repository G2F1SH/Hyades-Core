package com.hyades.client.api;

/**
 * 其他模组可实现的客户端扩展入口点。
 * <p>
 * 在自身的 {@code fabric.mod.json} 中声明（无需依赖我们的初始化顺序）：
 * <pre>{@code
 * "entrypoints": {
 *     "hyades-core-client": ["com.example.mymod.MyHyadesExtension"]
 * }
 * }</pre>
 * 并在 {@code depends} 中声明 {@code "hyades-core": ">=1.0.0"}。
 * 该方法会在 Hyades Core 客户端基础初始化完成后被调用。
 */
public interface HyadesCoreClientExtension {

    /** Hyades Core 客户端初始化完成后回调 */
    void onHyadesCoreInitialize(HyadesCoreAPI api);
}