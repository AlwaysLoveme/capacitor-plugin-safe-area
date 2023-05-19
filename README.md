# capacitor-plugin-safe-area

<p align="left">
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic">
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-IOS-516BEB?style=plastic">
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEA?logo=ios&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Capacitor v5-516BEA?style=plastic">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-safe-area">
    <img src="https://img.shields.io/npm/v/capacitor-plugin-safe-area/latest.svg">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-safe-area">
    <img src="https://img.shields.io/npm/dm/capacitor-plugin-safe-area.svg"/>
  </a>
</p>

a capacitor plugin to get SafeArea info on Android and IOS, now it's also support Capacitor v5 with version@2.0.0+

if you are using Capacitor 3.x , please install version 0.0.x , and version 1.x.x for Capacitor 4.x

> I'm glad if this plugin helped you, please give it a star

## Install

```bash
npm install capacitor-plugin-safe-area
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

// when safe-area changed
const eventListener = await SafeArea.addListener('safeAreaChanged', data => {
  const { insets } = data;
  document.documentElement.style.setProperty(
    '--safe-area-top',
    `${insets.top}px`,
  );
  document.documentElement.style.setProperty(
    '--safe-area-right',
    `${insets.right}px`,
  );
  document.documentElement.style.setProperty(
    '--safe-area-left',
    `${insets.left}px`,
  );
  document.documentElement.style.setProperty(
    '--safe-area-bottom',
    `${insets.bottom}`,
  );
});
eventListener.remove();
```

## API

<docgen-index>

- [`getSafeAreaInsets()`](#getsafeareainsets)
- [`getStatusBarHeight()`](#getstatusbarheight)
- [`addListener('safeAreaChanged', ...)`](#addlistenersafeareachanged)
- [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getSafeAreaInsets()

```typescript
getSafeAreaInsets() => Promise<SafeAreaInsets>
```

**Returns:** <code>Promise&lt;<a href="#safeareainsets">SafeAreaInsets</a>&gt;</code>

---

### getStatusBarHeight()

```typescript
getStatusBarHeight() => Promise<StatusBarInfo>
```

**Returns:** <code>Promise&lt;<a href="#statusbarinfo">StatusBarInfo</a>&gt;</code>

---

### addListener('safeAreaChanged', ...)

```typescript
addListener(event: 'safeAreaChanged', listenerFunc: (data: SafeAreaInsets) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

event listener when safe-area changed

| Param              | Type                                                                         |
| ------------------ | ---------------------------------------------------------------------------- |
| **`event`**        | <code>'safeAreaChanged'</code>                                               |
| **`listenerFunc`** | <code>(data: <a href="#safeareainsets">SafeAreaInsets</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

---

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
