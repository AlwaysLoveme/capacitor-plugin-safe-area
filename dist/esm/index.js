import { registerPlugin } from '@capacitor/core';
const SafeArea = registerPlugin('SafeArea', {
    web: () => import('./web').then(m => new m.SafeAreaWeb()),
});
export * from './definitions';
export { SafeArea };
//# sourceMappingURL=index.js.map