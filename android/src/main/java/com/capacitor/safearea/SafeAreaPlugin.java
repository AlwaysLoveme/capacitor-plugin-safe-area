package com.capacitor.safearea;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.graphics.drawable.ColorDrawable;
import android.content.res.Configuration;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.Objects;

@CapacitorPlugin(name = "SafeArea")
public class SafeAreaPlugin extends Plugin {

    private static final String KEY_INSET = "insets";
    private static final String Bar_Height = "statusBarHeight";
    private final SafeArea safeAreaInsets = new SafeArea();

    private OrientationEventListener orientationEventListener;
    private boolean isListening = false;

    private int lastOrientation = -1;
    private Handler mainHandler;
    
    // 简化后的状态管理
    private JSObject lastInsets = null;
    private Runnable pendingRotationTask = null;


    @Override
    public void load() {
        if (this.getBridge() != null) {
            safeAreaInsets.setBridge(this.getBridge());
            mainHandler = new Handler(Looper.getMainLooper());
            this.startListeningForSafeAreaChanges();
        }
    }

    @Override
    protected void handleOnDestroy() {
        stopListening();
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        super.handleOnDestroy();
    }

    private void startListeningForSafeAreaChanges() {
        if (!isListening && bridge != null && bridge.getActivity() != null) {
            // 初始化：记录当前状态，避免初始触发事件
            lastInsets = safeAreaInsets.getSafeAreaInsets(false);
            lastOrientation = getCurrentRotation();
            
            // 使用 OrientationEventListener 监听屏幕旋转
            // 相比 Configuration.onConfigurationChanged，它能更早地检测到旋转
            orientationEventListener = new OrientationEventListener(bridge.getActivity()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    handleOrientationChange();
                }
            };
            
            if (orientationEventListener.canDetectOrientation()) {
                orientationEventListener.enable();
            }

            isListening = true;
        }
    }

    /**
     * 处理方向变化
     * 使用 debounce 模式：取消之前的待处理任务，重新调度
     */
    private void handleOrientationChange() {
        int currentOrientation = getCurrentRotation();
        
        // 检查方向是否真正改变（排除初始化和无效值）
        if (currentOrientation == lastOrientation || 
            currentOrientation == -1 || 
            lastOrientation == -1) {
            // 首次初始化：记录方向但不触发
            if (lastOrientation == -1 && currentOrientation != -1) {
                lastOrientation = currentOrientation;
            }
            return;
        }
        
        // 更新方向
        lastOrientation = currentOrientation;
        
        // Debounce: 取消之前的待处理任务
        if (pendingRotationTask != null && mainHandler != null) {
            mainHandler.removeCallbacks(pendingRotationTask);
        }
        
        // 创建新的延迟任务
        pendingRotationTask = () -> {
            notifySafeAreaChange();
            pendingRotationTask = null;
        };
        
        // 延迟执行，等待旋转动画完成和 insets 更新
        if (mainHandler != null) {
            mainHandler.postDelayed(pendingRotationTask, 200);
        }
    }

    /**
     * 通知 SafeArea 变化
     * 只在值真正改变时才通知
     */
    private void notifySafeAreaChange() {
        if (bridge == null || bridge.getActivity() == null) {
            return;
        }
        
        // 在主线程获取最新的 insets
        bridge.getActivity().runOnUiThread(() -> {
            JSObject currentInsets = safeAreaInsets.getSafeAreaInsets(false);
            
            // 只在值改变时才通知
            if (!insetsEqual(lastInsets, currentInsets)) {
                lastInsets = currentInsets;
                
                JSObject ret = new JSObject();
                ret.put(KEY_INSET, currentInsets);
                notifyListeners("safeAreaChanged", ret);
            }
        });
    }

    /**
     * 停止所有监听
     */
    private void stopListening() {
        if (isListening) {
            if (orientationEventListener != null) {
                orientationEventListener.disable();
                orientationEventListener = null;
            }
            
            if (mainHandler != null && pendingRotationTask != null) {
                mainHandler.removeCallbacks(pendingRotationTask);
                pendingRotationTask = null;
            }
            
            isListening = false;
        }
    }

    private int getCurrentRotation() {
        if (bridge == null || bridge.getActivity() == null) {
            return -1;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Use new API for Android 11+
            try {
                return bridge.getActivity().getDisplay().getRotation();
            } catch (Exception e) {
                Log.w("SafeAreaPlugin", "Failed to get rotation using new API, falling back", e);
            }
        }
        
        // Fallback for older versions
        try {
            return bridge.getActivity().getWindowManager().getDefaultDisplay().getRotation();
        } catch (Exception e) {
            Log.w("SafeAreaPlugin", "Failed to get rotation", e);
            return -1;
        }
    }


    @SuppressWarnings("unused")
    @PluginMethod
    public void setImmersiveNavigationBar(PluginCall call) {
        if (bridge == null || bridge.getActivity() == null) {
            call.reject("Activity not available");
            return;
        }
        
        Window window = bridge.getActivity().getWindow();
        if (window == null) {
            call.reject("Window not available");
            return;
        }
        
        // optional style parameter: default | light | dark
        String styleStr = call.getString("statusBarStyle");
        final String styleFinal = styleStr != null ? styleStr : "default";

        // prefer platform API for API 34+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            bridge.getActivity().runOnUiThread(() -> {
                WindowCompat.setDecorFitsSystemWindows(window, false);
                Objects.requireNonNull(window.getInsetsController()).setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
                // hide system bars using compat controller for consistency
                WindowInsetsControllerCompat compat = new WindowInsetsControllerCompat(window, window.getDecorView());
                compat.hide(WindowInsetsCompat.Type.systemBars());
                window.getDecorView().setBackgroundColor(Color.TRANSPARENT);

                // Determine appearance for status bar icons based on styleFinal (auto/dark/light)
                View decor = window.getDecorView();
                boolean wantDarkIcons = computeWantDarkIcons(decor, null, styleFinal);
                compat.setAppearanceLightStatusBars(wantDarkIcons);
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bridge.getActivity().runOnUiThread(() -> {
                WindowCompat.setDecorFitsSystemWindows(window, false);
                WindowInsetsControllerCompat compat = new WindowInsetsControllerCompat(window, window.getDecorView());
                compat.hide(WindowInsetsCompat.Type.systemBars());
                window.getDecorView().setBackgroundColor(Color.TRANSPARENT);

                // Determine appearance for status bar icons
                View decor = window.getDecorView();
                boolean wantDarkIcons = computeWantDarkIcons(decor, null, styleFinal);
                compat.setAppearanceLightStatusBars(wantDarkIcons);
            });
        } else {
            // fallback for very old devices: use legacy systemUiVisibility flags
            bridge.getActivity().runOnUiThread(() -> {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                );
                // cannot reliably set icon color on legacy devices
            });
        }
        call.resolve();
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void unsetImmersiveNavigationBar(PluginCall call) {
        if (bridge == null || bridge.getActivity() == null) {
            call.reject("Activity not available");
            return;
        }
        
        Window window = bridge.getActivity().getWindow();
        if (window == null) {
            call.reject("Window not available");
            return;
        }

        // Read optional color parameters from the call
        String bgColorStr = call.getString("statusBarBg");
        // enum: default, light, dark
        String styleStr = call.getString("statusBarStyle");
        Integer tmpBg = null;
        try {
            if (bgColorStr != null) {
                tmpBg = android.graphics.Color.parseColor(bgColorStr);
            }
        } catch (IllegalArgumentException ignored) {
            // ignore invalid color string
        }
        final Integer bgColorIntFinal = tmpBg; // may be null => we'll default to transparent
        final String styleFinal = styleStr != null ? styleStr : "default";

        // Reverse the immersive/navigation bar changes applied in setImmersiveNavigationBar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            bridge.getActivity().runOnUiThread(() -> {
                // restore default fitting of system windows
                WindowCompat.setDecorFitsSystemWindows(window, true);
                // clear any explicit appearance flags if present
                if (window.getInsetsController() != null) {
                    window.getInsetsController().setSystemBarsAppearance(
                            0,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    );
                }

                View decor = window.getDecorView();
                // show system bars via compat controller
                WindowInsetsControllerCompat compat = new WindowInsetsControllerCompat(window, decor);
                compat.show(WindowInsetsCompat.Type.systemBars());

                // Determine background: if not provided, default to transparent
                final int appliedBg = bgColorIntFinal != null ? bgColorIntFinal : Color.TRANSPARENT;
                decor.setBackgroundColor(appliedBg);

                // Determine appearance (icons/text): styleFinal can be 'default', 'light', 'dark'
                boolean wantDarkIcons = computeWantDarkIcons(decor, bgColorIntFinal, styleFinal);

                // Apply appearance: compat.setAppearanceLightStatusBars(true) => dark icons
                compat.setAppearanceLightStatusBars(wantDarkIcons);
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bridge.getActivity().runOnUiThread(() -> {
                WindowCompat.setDecorFitsSystemWindows(window, true);
                View decor = window.getDecorView();
                WindowInsetsControllerCompat compat = new WindowInsetsControllerCompat(window, decor);
                compat.show(WindowInsetsCompat.Type.systemBars());

                // Determine background: if not provided, default to transparent
                final int appliedBg = bgColorIntFinal != null ? bgColorIntFinal : Color.TRANSPARENT;
                try {
                    window.setStatusBarColor(appliedBg);
                } catch (Throwable t) {
                    decor.setBackgroundColor(appliedBg);
                }

                // Determine appearance
                boolean wantDarkIcons = computeWantDarkIcons(decor, bgColorIntFinal, styleFinal);
                compat.setAppearanceLightStatusBars(wantDarkIcons);
            });
        } else {
            // fallback for very old devices
            bridge.getActivity().runOnUiThread(() -> {
                legacySetSystemUiVisibility(window.getDecorView());
                // best-effort apply background via decor view for legacy devices
                final int appliedBg = bgColorIntFinal != null ? bgColorIntFinal : Color.TRANSPARENT;
                window.getDecorView().setBackgroundColor(appliedBg);
                // cannot reliably set icon color on legacy devices
            });
        }

        call.resolve();
    }

    // Helper: compute normalized luminance of a color (0..1)
    private double getLuminance(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
    }

    // Helper: compute whether we want dark status-bar icons/text
    // Returns true when we should request dark icons (i.e. suitable for light backgrounds)
    private boolean computeWantDarkIcons(View decor, Integer bgColor, String style) {
        // explicit style override
        if ("light".equalsIgnoreCase(style)) {
            return false; // light icons/text requested
        }
        if ("dark".equalsIgnoreCase(style)) {
            return true; // dark icons/text requested
        }

        // auto: prefer bgColor if provided
        try {
            if (bgColor != null) {
                return getLuminance(bgColor) >= 0.5;
            }
            if (decor != null && decor.getBackground() instanceof ColorDrawable) {
                int existing = ((ColorDrawable) decor.getBackground()).getColor();
                return getLuminance(existing) >= 0.5;
            }
        } catch (Throwable ignored) {
            // fall through to system theme
        }

        // final fallback: system night mode
        if (bridge != null && bridge.getContext() != null) {
            try {
                int uiMode = bridge.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                return (uiMode != Configuration.UI_MODE_NIGHT_YES);
            } catch (Exception ignored) {
                // fall through to default
            }
        }
        
        // Ultimate fallback: assume light background (dark icons)
        return true;
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void startListeningForSafeAreaChanges(PluginCall call) {
        this.startListeningForSafeAreaChanges();
        call.resolve();
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void stopListeningForSafeAreaChanges(PluginCall call) {
        stopListening();
        call.resolve();
    }


    /**
     * 比较两个 insets 对象是否相等
     */
    private boolean insetsEqual(JSObject insets1, JSObject insets2) {
        if (insets1 == null || insets2 == null) {
            return false;
        }
        try {
            return insets1.getInteger("top").equals(insets2.getInteger("top")) &&
                   insets1.getInteger("left").equals(insets2.getInteger("left")) &&
                   insets1.getInteger("right").equals(insets2.getInteger("right")) &&
                   insets1.getInteger("bottom").equals(insets2.getInteger("bottom"));
        } catch (Exception e) {
            return false;
        }
    }


    @SuppressWarnings("unused")
    @PluginMethod
    public void getSafeAreaInsets(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put(KEY_INSET, safeAreaInsets.getSafeAreaInsets());
        call.resolve(ret);
    }

    @SuppressWarnings("unused")
    @PluginMethod
    public void getStatusBarHeight(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put(Bar_Height, safeAreaInsets.getStatusBarHeight());
        call.resolve(ret);
    }

    private void legacySetSystemUiVisibility(View decorView) {
        // Encapsulate deprecated call for very old API levels
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}
