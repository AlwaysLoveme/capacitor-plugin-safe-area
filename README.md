# capacitor-plugin-safe-area

<p align="left">
<a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
<img src="https://img.shields.io/badge/support-Android-516BEB?style=plastic"/>
</a>
<a href="https://img.shields.io/badge/support-Android-516BEB?logo=android&logoColor=white&style=plastic">
<img src="https://img.shields.io/badge/support-IOS-516BEB?style=plastic"/>
</a>
<a href="https://img.shields.io/badge/npm-516BEB?style=plastic">
<img src="https://img.shields.io/badge/npm-V0.0.6-516BEB?style=plastic"/>
</a>
</p>

a capacitor V3/V4 plugin to get safeArea info on Android and IOS

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

SafeArea.getStatusBarHeight().then(({statusBarHeight}) => {
  console.log(statusBarHeight, 'statusbarHeight');
})
```

## API

<docgen-index>

* [`getSafeAreaInsets()`](#getsafeareainsets)
* [`getStatusBarHeight()`](#getstatusbarheight)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getSafeAreaInsets()

```typescript
getSafeAreaInsets() => Promise<SafeArea>
```

**Returns:** <code>SafeArea</code>

--------------------


### getStatusBarHeight()

```typescript
getStatusBarHeight() => Promise<StatusBarInfo>
```

**Returns:** <code>StatusBarInfo</code>

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

</docgen-api>
