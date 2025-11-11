package com.capacitor.safearea;

import android.util.Log;

import android.os.Build;
import android.os.Looper;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;

import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.graphics.Rect;

import androidx.core.view.DisplayCutoutCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.graphics.Insets;

public class SafeArea {
    private Bridge bridge;

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    /**
     * 检查当前是否在主线程
     */
    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public JSObject getSafeAreaInsets() {
        return getSafeAreaInsets(false);
    }

    /**
     * 获取 SafeArea insets
     * @param forceRefresh 是否强制刷新 WindowInsets（仅在旋转等场景使用，避免无限循环）
     */
    public JSObject getSafeAreaInsets(boolean forceRefresh) {
        // Null check for bridge and activity
        if (this.bridge == null || this.bridge.getActivity() == null) {
            return this.result(0, 0, 0, 0);
        }

        // Try to use WindowInsetsCompat via ViewCompat for best cross-version behavior.
        View decor = this.bridge.getActivity().getWindow().getDecorView();
        if (decor == null) {
            return this.result(0, 0, 0, 0);
        }

        // 只有在明确需要强制刷新时才请求重新应用 WindowInsets
        // 避免在监听器回调中再次触发，导致无限循环
        if (forceRefresh) {
            if (isMainThread()) {
                ViewCompat.requestApplyInsets(decor);
            } else {
                // 如果不在主线程，在主线程执行（不等待结果）
                this.bridge.getActivity().runOnUiThread(() -> {
                    ViewCompat.requestApplyInsets(decor);
                });
            }
        }

        int top = 0;
        int left = 0;
        int right = 0;
        int bottom = 0;

        try {
            WindowInsetsCompat insetsCompat = ViewCompat.getRootWindowInsets(decor);
            if (insetsCompat != null) {
                Insets sys = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
                top = sys.top;
                left = sys.left;
                right = sys.right;
                bottom = sys.bottom;

                // If there's a display cutout (notch), ensure we include its safe insets
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DisplayCutoutCompat dc = insetsCompat.getDisplayCutout();
                    if (dc != null) {
                        top = Math.max(dc.getSafeInsetTop(), top);
                        left = Math.max(dc.getSafeInsetLeft(), left);
                        right = Math.max(dc.getSafeInsetRight(), right);
                        bottom = Math.max(dc.getSafeInsetBottom(), bottom);
                    }
                }

                return this.result(top, left, right, bottom);
            }
        } catch (Throwable t) {
            Log.i(SafeAreaPlugin.class.toString(), "WindowInsetsCompat failed, falling back: " + t);
        }

        // Fallback: try platform WindowInsets (may be null) and then visible frame
        try {
            WindowInsets winInsets = decor.getRootWindowInsets();
            if (winInsets != null) {
                top = winInsets.getStableInsetTop();
                left = winInsets.getStableInsetLeft();
                right = winInsets.getStableInsetRight();
                bottom = winInsets.getStableInsetBottom();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DisplayCutout displayCutout = winInsets.getDisplayCutout();
                    if (displayCutout != null) {
                        top = Math.max(displayCutout.getSafeInsetTop(), top);
                        left = Math.max(displayCutout.getSafeInsetLeft(), left);
                        right = Math.max(displayCutout.getSafeInsetRight(), right);
                        bottom = Math.max(displayCutout.getSafeInsetBottom(), bottom);
                    }
                }

                return this.result(top, left, right, bottom);
            }
        } catch (Throwable t) {
            Log.i(SafeAreaPlugin.class.toString(), "Platform WindowInsets failed, falling back to visible frame: " + t);
        }

        // Final fallback: use window visible display frame (Rect.top is usually status bar height)
        try {
            Rect r = new Rect();
            decor.getWindowVisibleDisplayFrame(r);
            top = Math.max(0, r.top);
        } catch (Throwable t) {
            Log.i(SafeAreaPlugin.class.toString(), "Visible frame fallback also failed: " + t);
        }

        return this.result(top, left, right, bottom);
    }

    // 判断是否是沉浸式状态栏
    @SuppressWarnings("unused")
    private Boolean getStatusBarVisible() {
        try {
            View decor = this.bridge.getActivity().getWindow().getDecorView();
            WindowInsetsCompat insetsCompat = ViewCompat.getRootWindowInsets(decor);
            if (insetsCompat != null) {
                Insets sys = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
                return sys.top == 0;
            }
            WindowInsets win = decor.getRootWindowInsets();
            return win != null && win.getStableInsetTop() == 0;
        } catch (Throwable ignored) {
            return false;
        }
    }

    // 判断是否是沉浸式导航栏
    @SuppressWarnings("unused")
    private Boolean tabBarIsVisible() {
        try {
            View decor = this.bridge.getActivity().getWindow().getDecorView();
            WindowInsetsCompat insetsCompat = ViewCompat.getRootWindowInsets(decor);
            if (insetsCompat != null) {
                Insets sys = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
                return sys.bottom > 0 || sys.left > 0 || sys.right > 0;
            }
            WindowInsets win = decor.getRootWindowInsets();
            return win != null && win.getStableInsetBottom() > 0;
        } catch (Throwable ignored) {
            return true;
        }
    }

    public int getStatusBarHeight() {
        // Keep existing behavior but try to reuse getSafeAreaInsets() logic to avoid duplication
        JSObject insets = getSafeAreaInsets();
        if (insets != null && insets.getInteger("top") != null) {
            return insets.getInteger("top");
        }
        return 0;
    }

    public JSObject result(int top, int left, int right, int bottom) {
        JSObject json = new JSObject();
        // Convert physical pixels to density-independent pixels (dp)
        json.put("top", pxToDp(top));
        json.put("left", pxToDp(left));
        json.put("right", pxToDp(right));
        json.put("bottom", pxToDp(bottom));
        return json;
    }

    private int pxToDp(int px) {
        float density = this.getDensity();
        // Convert physical pixels to dp (density-independent pixels)
        return Math.round(px / density);
    }

    private float getDensity() {
        if (this.bridge == null || this.bridge.getActivity() == null) {
            return 1.0f;
        }
        return this.bridge.getActivity().getResources().getDisplayMetrics().density;
    }
}
