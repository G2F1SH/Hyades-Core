# Hyades Core 使用文档

Hyades Core 是一个 Fabric **前置模组（Library / Framework）**，为其它模组提供：

- 客户端功能模块系统（开关、按键绑定、设置项）
- HUD 元素系统（渲染、拖拽编辑）
- ClickGUI与全局 HUD 编辑模式

本文档面向**想基于 Hyades Core 开发功能的模组作者**。

---

## 1. 快速开始：作为前置模组接入

### 1.1 添加依赖

在你的模组项目 `build.gradle` 中：

```groovy
repositories {
	mavenLocal()          // 依赖通过 gradlew publishToMavenLocal 发布到本地
	maven { url = 'https://maven.fabricmc.net/' }
}

dependencies {
	minecraft "com.mojang:minecraft:${project.minecraft_version}"
	// ...其它依赖

	// Hyades Core（注意：此 Loom 版本使用 implementation 而非 modImplementation）
	implementation "com.hyades:hyades-core:1.0.0"
}
```

> **映射命名空间**：Hyades Core 发布时使用 named/mojmap 映射。消费方在相同的开发映射下
> 直接用 `implementation` 即可编译，无需 `modImplementation`。

### 1.2 声明依赖关系（fabric.mod.json）

```json
{
	"schemaVersion": 1,
	"id": "your-mod-id",
	"version": "1.0.0",
	"environment": "client",
	"entrypoints": {
		"hyades-core-client": [
			"com.example.mymod.MyHyadesExtension"
		]
	},
	"depends": {
		"fabricloader": ">=0.19.3",
		"minecraft": "~26.2",
		"java": ">=25",
		"hyades-core": ">=1.0.0"
	}
}
```

### 1.3 实现扩展入口点

```java
package com.example.mymod;

import com.hyades.client.api.HyadesCoreAPI;
import com.hyades.client.api.HyadesCoreClientExtension;
import com.hyades.client.module.Category;

public class MyHyadesExtension implements HyadesCoreClientExtension {

	@Override
	public void onHyadesCoreInitialize(HyadesCoreAPI api) {
		// Hyades Core 完成自身初始化后调用，可在此注册模块 / HUD
		api.registerModule(new MyModule());
		api.registerHudElement(new MyHudElement(), "MyHud");
	}
}
```

> 入口点类需要无参构造。若只用到 `HyadesCoreAPI`，也可在普通 `ClientModInitializer` 中
> 直接访问 `HyadesCoreAPI.INSTANCE`。

---

## 2. 核心 API：`HyadesCoreAPI`

单例 `HyadesCoreAPI.INSTANCE`，方法一览：

| 方法 | 作用 |
|---|---|
| `registerModule(Module)` | 注册一个模块，立即出现在 ClickGUI 对应分类 |
| `createModule(name, desc, category)` | 便捷创建并注册一个 `SimpleModule` |
| `getModuleManager()` | 模块管理器单例 |
| `registerHudElement(HudElement, displayName)` | 注册 HUD 元素，返回其包装模块（控制显隐） |
| `getHudRenderer()` | HUD 渲染器单例 |
| `openClickGui()` | 打开 ClickGUI |
| `toggleHudDragMode()` | 切换 HUD 拖拽编辑模式 |
| `isHudDragMode()` | 是否处于 HUD 编辑模式 |

### 模块管理器 `ModuleManager`

```java
ModuleManager mm = api.getModuleManager();
mm.getModules();                        // 全部模块
mm.getModulesByCategory(Category.MISC); // 按分类查询
mm.getModule("Sprint");                 // 按名称查询（忽略大小写）
mm.setModuleEnabled(module, true);      // 开关模块
```

---

## 3. 开发功能模块

### 3.1 方式一：`SimpleModule`（推荐，免子类化）

```java
import com.hyades.client.module.Category;
import com.hyades.client.module.SimpleModule;
import com.hyades.client.setting.NumberSetting;
import org.lwjgl.glfw.GLFW;

// 在扩展入口点中：
SimpleModule module = api.createModule("MyFeature", "我的功能描述", Category.MISC)
		.withKeyBind(GLFW.GLFW_KEY_J)
		.onEnable(() -> {
			// 模块开启时执行
		})
		.onDisable(() -> {
			// 模块关闭时执行
		});

// 追加设置项
module.addSetting(new NumberSetting("Speed", "速度", 1.0, 0.1, 10.0, 0.1));
```

### 3.2 方式二：继承 `Module`

```java
package com.example.mymod;

import com.hyades.client.module.Category;
import com.hyades.client.module.Module;
import com.hyades.client.setting.BooleanSetting;

public class MyModule extends Module {

	public MyModule() {
		super("MyModule", "模块描述", Category.MISC);
		this.addSetting(new BooleanSetting("Verbose", "输出更多日志", false));
	}

	@Override
	public void onEnable() {
		// 逻辑…
	}

	@Override
	public void onDisable() {
		// 逻辑…
	}
}
```

`Module` 常用成员：

| 成员 | 说明 |
|---|---|
| `getName()` / `getDescription()` / `getCategory()` | 元信息 |
| `isEnabled()` / `toggle()` / `enable()` / `disable()` | 状态控制 |
| `setKeyBind(int glfwKeyCode)` | 按键绑定（`GLFW.GLFW_KEY_*`），`0` 表示无 |
| `addSetting(Setting<?>)` / `getSettings()` | 设置项 |
| `getSetting("Name")` | 按名称取设置项（泛型） |
| `onEnable()` / `onDisable()` | 开关回调（覆写点） |

模块分类枚举 `Category`：`COMBAT`、`MOVEMENT`、`PLAYER`、`RENDER`、`MISC`、`HUD`。

### 3.3 设置项（Settings）

| 类型 | 构造 | 取值 |
|---|---|---|
| `BooleanSetting` | `(name, desc, defaultValue)` | `isEnabled()` / `set(bool)` / `toggle()` |
| `NumberSetting` | `(name, desc, defaultValue, min, max, step)` | `getDouble()` / `getInt()` / `getFloat()` / `set(v)` / `increment()` / `decrement()` |
| `ModeSetting` | `(name, desc, String[] modes, defaultValue)` | `getMode()` / `setMode(s)` / `cycle()` |
| `MultiSelectSetting` | `(name, desc, String[] options, String[] defaults)` | `isEnabled(opt)` / `toggle(opt)` / `getSelected()` |

```java
// 运行时读取设置
BooleanSetting verbose = module.getSetting("Verbose");
if (verbose.isEnabled()) { … }
```

> 按键轮询与 ClickGUI 面板会自动接管已注册模块及其设置项，无需额外接线。

---

## 4. 开发 HUD 元素

继承 `HudElement`，实现三个抽象成员：

```java
package com.example.mymod;

import com.hyades.client.hud.HudElement;
import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontPresets;

public class MyHudElement extends HudElement {

	public MyHudElement() {
		super("MyHud", 20, 20); // (id, 初始x, 初始y)
	}

	@Override
	public float getWidth() {
		return 100;
	}

	@Override
	public float getHeight() {
		return 20;
	}

	@Override
	protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
		Paint paint = new Paint().setColor(0xFFFFFFFF);
		ctx.drawString("Hello", this.x, this.y, FontPresets.defaultFont(8.5f), paint);
	}
}
```

注册（会同时生成一个 `HUD` 分类的模块，开关即控制显隐）：

```java
api.registerHudElement(new MyHudElement(), "MyHud");
```

`HudElement` 常用成员：`getX()/getY()`、`setPosition(x, y)`、`isVisible()/setVisible(b)`、
`contains(px, py)`（命中测试）、`render(ctx, mouseX, mouseY, deltaTicks)`。

玩家在游戏中按 **End** 进入 HUD 编辑模式后，可直接拖拽已启用的 HUD 元素。

### 绘制 API：`DrawContext`（类 Android Canvas）

在 `HudElement.renderElement` 中收到的 `DrawContext ctx` 是唯一的绘制入口：

```java
Paint paint = new Paint().setColor(0xFF4CAF50);

ctx.fillRect(10, 10, 100, 20, 0x66000000);                     // 填充矩形
ctx.outlineRect(10, 10, 100, 20, 1.0f, 0xFF4CAF50);            // 描边矩形
ctx.drawRoundedRect(10, 40, 100, 20, 4.0f, 0x66FFFFFF);        // 圆角矩形
ctx.drawGradientVertical(10, 70, 100, 20, 0xFF4CAF50, 0xFF2196F3);
ctx.drawLine(10, 100, 110, 100, 1.5f, 0xFFFFFFFF);             // 线段
ctx.drawCircleFilled(30, 130, 8, 0xFFFF5722);                  // 实心圆
ctx.drawString("Hello", 10, 150, FontPresets.defaultFont(8.5f), paint); // 文本

ctx.save(); ctx.translate(20, 20); ctx.rotate(45); /* … */ ctx.restore(); // 变换
ctx.clip(0, 0, 100, 100); /* … */ ctx.popClip();                          // 裁剪
```

颜色为 `0xAARRGGBB` 格式的 `int`。字体用 `FontPresets`（`defaultFont` / `axiforma` /
`axiformaBold` / `materialIcons` / `zenIcon`，均传字号）。

---

## 5. 内置功能参考

| 功能 | 说明 |
|---|---|
| ClickGUI | 默认 **Right Shift** 打开，按分类列出模块与设置 |
| HUD 编辑模式 | 默认 **End** 切换，开启后可拖拽 HUD 元素 |
| 内置模块 | `Sprint`、`Speed`、`NoFall`、`Fullbright` |
| 内置 HUD | `Watermark`、`ModuleList`、`KeyBinds`、`Target` |

---

## 6. 发布与版本

发布到本地 Maven 仓库（供消费方 `mavenLocal()` + `implementation` 引用）：

```bash
gradlew build publishToMavenLocal
```

产物坐标：`com.hyades:hyades-core:1.0.0`（含 sources jar）。
版本号在根目录 `gradle.properties` 的 `version=` 中修改；升级版本后记得同步
消费方 `fabric.mod.json` 中 `depends.hyades-core` 的约束。
