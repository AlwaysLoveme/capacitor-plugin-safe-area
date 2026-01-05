# capacitor-plugin-safe-area

<p align="left">
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic"  alt="" >
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-IOS-516BEB?style=plastic" alt="">
  </a>
  <a href="https://img.shields.io/badge/support-Android-516BEA?logo=ios&logoColor=white&style=plastic">
    <img src="https://img.shields.io/badge/support-Capacitor v7-516BEA?style=plastic" alt="">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-safe-area">
    <img src="https://img.shields.io/npm/v/capacitor-plugin-safe-area/latest.svg" alt="">
  </a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-safe-area">
    <img src="https://img.shields.io/npm/dm/capacitor-plugin-safe-area.svg" alt=""/>
  </a>
</p>

#### a capacitor plugin to get SafeArea info on Android and IOS, latest version is support for Capacitor v7.

### Version Support
- [x] v5.0.0 support Capacitor v8
- [x] v4.0.0 support Capacitor v7
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

## Usage

```typescript
import { SafeArea } from 'capacitor-plugin-safe-area';

SafeArea.getSafeAreaInsets().then(({ insets }) => {
  console.log(insets);
  for (const [key, value] of Object.entries(insets)) {
    document.documentElement.style.setProperty(
      `--safe-area-inset-${key}`,
      `${value}px`,
    );
  }
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
      `--safe-area-inset-${key}`,
      `${value}px`,
    );
  }
});
```

## Use with TailwindCSS
 Use `TailwindCSS` with the `plugin`:  [https://github.com/mahyarmirrashed/tailwindcss-safe-area-capacitor](https://github.com/mahyarmirrashed/tailwindcss-safe-area-capacitor)

 For more usage, please refer to the plugin repo

```tsx
import {useEffect} from 'react';
import { SafeArea } from 'capacitor-plugin-safe-area';

import type {FC} from 'react';

const App = () => {
    useEffect(() => {
        (async function(){
            const safeAreaData = await SafeArea.getSafeAreaInsets();
            const {insets} = safeAreaData;
            for (const [key, value] of Object.entries(insets)) {
                document.documentElement.style.setProperty(
                    `--safe-area-inset-${key}`,
                    `${value}px`,
                );
            }
        })()
    }, []);
    return (
        <div className="pb-safe toolbar">
            <div>....</div>
        </div>
    )
}
export default App;
```

## API

<docgen-index>

* [`getSafeAreaInsets()`](#getsafeareainsets)
* [`getStatusBarHeight()`](#getstatusbarheight)
* [`setImmersiveNavigationBar(...)`](#setimmersivenavigationbar)
* [`unsetImmersiveNavigationBar(...)`](#unsetimmersivenavigationbar)
* [`addListener('safeAreaChanged', ...)`](#addlistenersafeareachanged-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

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


### setImmersiveNavigationBar(...)

```typescript
setImmersiveNavigationBar(options?: Pick<NavigationBarOptions, "statusBarStyle"> | undefined) => Promise<void>
```

Set navigation bar immersive on Android , statusbar background is always set to transparent, not implemented on IOS

| Param         | Type                                                                                                                    |
| ------------- | ----------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#pick">Pick</a>&lt;<a href="#navigationbaroptions">NavigationBarOptions</a>, 'statusBarStyle'&gt;</code> |

--------------------


### unsetImmersiveNavigationBar(...)

```typescript
unsetImmersiveNavigationBar(options?: NavigationBarOptions | undefined) => Promise<void>
```

unset navigation bar immersive on Android , not implemented on IOS

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#navigationbaroptions">NavigationBarOptions</a></code> |

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


#### NavigationBarOptions

| Prop                 | Type                                                      | Description                                        |
| -------------------- | --------------------------------------------------------- | -------------------------------------------------- |
| **`statusBarBg`**    | <code>string</code>                                       | statusbar background color, default is transparent |
| **`statusBarStyle`** | <code><a href="#statusbarstyle">StatusbarStyle</a></code> | statusbar style                                    |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


### Type Aliases


#### Pick

From T, pick a set of properties whose keys are in the union K

<code>{
 [P in K]: T[P];
 }</code>


### Enums


#### StatusbarStyle

| Members     | Value                |
| ----------- | -------------------- |
| **`Light`** | <code>'light'</code> |
| **`Dark`**  | <code>'dark'</code>  |

</docgen-api>
