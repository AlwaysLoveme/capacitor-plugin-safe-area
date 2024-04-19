package com.capacitor.safearea;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.core.view.WindowCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "SafeArea")
public class SafeAreaPlugin extends Plugin {

    private static final String KEY_INSET = "insets";
    private static final String Bar_Height = "statusBarHeight";
    private final SafeArea safeAreaInsets = new SafeArea();

    private OrientationEventListener orientationEventListener;
    private boolean isListening = false;

    private int lastOrientation = -1;


    @Override
    public void load() {
        safeAreaInsets.setBridge(this.getBridge());
        this.startListeningForSafeAreaChanges();
    }

    private void startListeningForSafeAreaChanges() {
        if (!isListening) {
            orientationEventListener = new OrientationEventListener(bridge.getActivity()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    int currentOrientation = bridge.getActivity().getWindowManager().getDefaultDisplay().getRotation();
                    if (currentOrientation != lastOrientation) {
                        lastOrientation = currentOrientation;
                        detectSafeAreaChanges();
                    }
                }
            };
            orientationEventListener.enable();

            isListening = true;
        }
    }


    @PluginMethod
    public void setImmersiveNavigationBar(PluginCall call) {
        Window window = bridge.getActivity().getWindow();
        // Use a transparent status bar and nav bar, and place the window behind the status bar
        // and nav bar. Due to a chromium bug, we need to get the height of both bars
        // and add it to the safe area insets. The native plugin is used to get this info.
        // See https://bugs.chromium.org/p/chromium/issues/detail?id=1094366
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bridge.getActivity().runOnUiThread(() -> {
                window.setDecorFitsSystemWindows(false);
                window.setStatusBarColor(0);
                window.setNavigationBarColor(0);
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // On older versions of android setDecorFitsSystemWindows doesn't exist yet, but it can
            // be emulated with flags.
            // It still must be P or greater, as that is the min version for getting the insets
            bridge.getActivity().runOnUiThread(() -> {
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                );
                window.setStatusBarColor(0);
                window.setNavigationBarColor(0);
            });
        } else {
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
            });

        }
        call.resolve();
    }

    @PluginMethod
    public void startListeningForSafeAreaChanges(PluginCall call) {
        this.startListeningForSafeAreaChanges();
        call.resolve();
    }

    @PluginMethod
    public void stopListeningForSafeAreaChanges(PluginCall call) {
        if (isListening) {
            orientationEventListener.disable();
            isListening = false;
        }
        call.resolve();
    }

    private void detectSafeAreaChanges() {
        JSObject ret = new JSObject();
        ret.put(KEY_INSET, safeAreaInsets.getSafeAreaInsets());
        notifyListeners("safeAreaChanged", ret);
    }


    @PluginMethod
    public void getSafeAreaInsets(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put(KEY_INSET, safeAreaInsets.getSafeAreaInsets());
        call.resolve(ret);
    }

    @PluginMethod
    public void getStatusBarHeight(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put(Bar_Height, safeAreaInsets.getStatusBarHeight());
        call.resolve(ret);
    }
}

