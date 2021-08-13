export interface SafeAreaPlugin {
  getSafeAreaInsets(): Promise<SafeAreaInsets>;
}

interface SafeArea {
  top: number;
  right: number;
  bottom: number;
  left: number;
}
export interface SafeAreaInsets {
  insets: SafeArea
}