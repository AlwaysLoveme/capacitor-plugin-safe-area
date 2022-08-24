import { WebPlugin } from '@capacitor/core';
export class SafeAreaWeb extends WebPlugin {
    async getSafeAreaInsets() {
        return {
            insets: {
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
            }
        };
    }
    async getStatusBarHeight() {
        return {
            statusBarHeight: 0
        };
    }
}
//# sourceMappingURL=web.js.map