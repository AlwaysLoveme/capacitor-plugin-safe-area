import type { PluginListenerHandle } from '@capacitor/core';

export interface SafeAreaPlugin {
  /**
   *  get mobile SafeArea info
   */
  getSafeAreaInsets(): Promise<SafeAreaInsets>;
  /**
   * get mobile statusbar height
   */
  getStatusBarHeight(): Promise<StatusBarInfo>;
  /**
   * event listener when safe-area changed
   * @param event
   * @param listenerFunc
   */
  addListener(
    event: 'safeAreaChanged',
    listenerFunc: (data: SafeAreaInsets) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}

interface SafeArea {
  top: number;
  right: number;
  bottom: number;
  left: number;
}
export interface SafeAreaInsets {
  insets: SafeArea;
}

export interface StatusBarInfo {
  statusBarHeight: number;
}
