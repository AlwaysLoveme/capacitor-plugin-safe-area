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
   * Set navigation bar immersive on Android , statusbar background is always set to transparent, not implemented on IOS
   * @param options.statusBarStyle - statusbar style
   */
  setImmersiveNavigationBar(options?: Pick<NavigationBarOptions, 'statusBarStyle'>): Promise<void>;

  /**
   * unset navigation bar immersive on Android , not implemented on IOS
   * @param options.statusBarBg - statusbar background color, default is transparent
   * @param options.statusBarStyle - statusbar style
   */
  unsetImmersiveNavigationBar(options?: NavigationBarOptions): Promise<void>;

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

export enum StatusbarStyle {
  Light = 'light',
  Dark = 'dark',
}
export interface NavigationBarOptions {
  /**
   * statusbar background color, default is transparent
   */
  statusBarBg?: string;
  /**
   * statusbar style
   */
  statusBarStyle?: StatusbarStyle;
}
