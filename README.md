# capacitor-plugin-safe-area

<p align="left">
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic"  alt="" >
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-IOS-516BEB?style=plastic" alt="">
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEA?logo=ios&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Capacitor v6-516BEA?style=plastic" alt="">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-safe-area">
    <img src="https://img.shields.io/npm/v/capacitor-plugin-safe-area/latest.svg" alt="">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-safe-area">
    <img src="https://img.shields.io/npm/dm/capacitor-plugin-safe-area.svg" alt=""/>
  </a>
</p>

#### a capacitor plugin to get SafeArea info on Android and IOS, latest version is support for Capacitor v6.

### Version Support
- [x] v3.0.0 support Capacitor v6
- [x] v2.0.0 support Capacitor v5
- [x] v1.0.0 support Capacitor v4
- [x] v0.0.1 support Capacitor v3

> I'm very glad if the plugin helped you, please give it a star

## Install

```bash
npm install capacitor-plugin-safe-area@latest
npx cap sync
```

## Useage

```typescript
import { SafeArea } from 'capacitor-plugin-safe-area';

SafeArea.getSafeAreaInsets().then(({ insets }) => {
  console.log(insets);
});

SafeArea.getStatusBarHeight().then(({ statusBarHeight }) => {
  console.log(statusBarHeight, 'statusbarHeight');
});

await SafeArea.removeAllListeners();

// when safe-area changed
await SafeArea.addListener('safeAreaChanged', data => {
  const { insets } = data;
  for (const [key, value] of Object.entries(insets)) {
    document.documentElement.style.setProperty(
      `--safe-area-${key}`,
      `${value}px`,
    );
  }
});
```

## API

<docgen-index>

* [`getSafeAreaInsets()`](#getsafeareainsets)
* [`getStatusBarHeight()`](#getstatusbarheight)
* [`setImmersiveNavigationBar()`](#setimmersivenavigationbar)
* [`addListener('safeAreaChanged', ...)`](#addlistenersafeareachanged-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getSafeAreaInsets()

```typescript
getSafeAreaInsets() => Promise<SafeAreaInsets>
```

Get mobile <a href="#safearea">SafeArea</a> info

**Returns:** <code>Promise&lt;<a href="#safeareainsets">SafeAreaInsets</a>&gt;</code>

--------------------


### getStatusBarHeight()

```typescript
getStatusBarHeight() => Promise<StatusBarInfo>
```

Get mobile statusbar height

**Returns:** <code>Promise&lt;<a href="#statusbarinfo">StatusBarInfo</a>&gt;</code>

--------------------


### setImmersiveNavigationBar()

```typescript
setImmersiveNavigationBar() => Promise<void>
```

Set navigation bar immersive on Android , not implemented on IOS

--------------------


### addListener('safeAreaChanged', ...)

```typescript
addListener(event: 'safeAreaChanged', listenerFunc: (data: SafeAreaInsets) => void) => Promise<PluginListenerHandle>
```

Event listener when safe-area changed

| Param              | Type                                                                         |
| ------------------ | ---------------------------------------------------------------------------- |
| **`event`**        | <code>'safeAreaChanged'</code>                                               |
| **`listenerFunc`** | <code>(data: <a href="#safeareainsets">SafeAreaInsets</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all native listeners for this plugin

--------------------


### Interfaces


#### SafeAreaInsets

| Prop         | Type                                          |
| ------------ | --------------------------------------------- |
| **`insets`** | <code><a href="#safearea">SafeArea</a></code> |


#### SafeArea

| Prop         | Type                |
| ------------ | ------------------- |
| **`top`**    | <code>number</code> |
| **`right`**  | <code>number</code> |
| **`bottom`** | <code>number</code> |
| **`left`**   | <code>number</code> |


#### StatusBarInfo

| Prop                  | Type                |
| --------------------- | ------------------- |
| **`statusBarHeight`** | <code>number</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
