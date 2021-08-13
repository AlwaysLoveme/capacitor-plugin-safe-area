import { WebPlugin } from '@capacitor/core';
import type { SafeAreaPlugin, SafeAreaInsets } from './definitions';
export declare class SafeAreaWeb extends WebPlugin implements SafeAreaPlugin {
    getSafeAreaInsets(): Promise<SafeAreaInsets>;
}
