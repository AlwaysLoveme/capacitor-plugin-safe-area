import type { PluginListenerHandle } from '@capacitor/core'

export interface SafeAreaPlugin {
  /**
   *  Get mobile SafeArea info
   */
  getSafeAreaInsets (): Promise<SafeAreaInsets>;

  /**
   * Get mobile statusbar height
   */
  getStatusBarHeight (): Promise<StatusBarInfo>;

  /**
   * Set navigation bar immersive on Android , not implemented on IOS
   */
  setImmersiveNavigationBar (): Promise<void>;

  /**
   * Event listener when safe-area changed
   * @param event
   * @param listenerFunc
   */
  addListener (
    event: 'safeAreaChanged',
    listenerFunc: (data: SafeAreaInsets) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle;

  /**
   * Capacitor plugin method to remove all created listeners
   */
  removeAllListeners (): Promise<void>
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
