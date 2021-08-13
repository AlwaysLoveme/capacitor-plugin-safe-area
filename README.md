# capacitor-plugin-safe-area

a capactior3 plugin to get Statusbar Height on Android and get SafeArea info on IOS

## Install

```bash
npm install capacitor-plugin-safe-area
npx cap sync
```

## API

<docgen-index>

* [`getSafeAreaInsets()`](#getsafeareainsets)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getSafeAreaInsets()

```typescript
getSafeAreaInsets() => SafeAreaInsets
```

**Returns:** <code>SafeAreaInsets</code>

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

</docgen-api>
