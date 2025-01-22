import type { PluginListenerHandle } from '@capacitor/core';

export interface SafeAreaPlugin {
  /**
   *  Get mobile SafeArea info
   */
  getSafeAreaInsets(): Promise<SafeAreaInsets>;

  /**
   * Get mobile statusbar height
   */
  getStatusBarHeight(): Promise<StatusBarInfo>;

  /**
   * Set navigation bar immersive on Android , not implemented on IOS
   */
  setImmersiveNavigationBar(): Promise<void>;

  /**
   * Event listener when safe-area changed
   * @param event
   * @param listenerFunc
   */
  addListener(event: 'safeAreaChanged', listenerFunc: (data: SafeAreaInsets) => void): Promise<PluginListenerHandle>;

  /**
   * Remove all native listeners for this plugin
   */
  removeAllListeners(): Promise<void>;
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
