package com.zhuxian.safearea;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.FrameLayout;

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

    private Rect previousSafeArea;
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener;
    private boolean isListening = false;


    @Override
    public void load() {
        this.startListeningForSafeAreaChanges();
    }

    private void startListeningForSafeAreaChanges() {
        if (!isListening) {
            FrameLayout rootView = bridge.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

            layoutListener = this::detectSafeAreaChanges;
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

            View.OnApplyWindowInsetsListener insetsListener = (v, insets) -> {
                detectSafeAreaChanges();
                return insets;
            };
            rootView.setOnApplyWindowInsetsListener(insetsListener);

            isListening = true;
        }
    }

    @PluginMethod
    public void stopListeningForSafeAreaChanges(PluginCall call) {
        if (isListening) {
            FrameLayout rootView = bridge.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
            rootView.setOnApplyWindowInsetsListener(null);
            isListening = false;
        }
        call.resolve();
    }

    private void detectSafeAreaChanges() {
//        FrameLayout rootView = bridge.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//        Rect safeArea = new Rect();
//        rootView.getWindowVisibleDisplayFrame(safeArea);
//
//        // 获取 DisplayCutout，用于判断是否有刘海屏等特殊屏幕形状
//        DisplayCutout displayCutout = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                displayCutout = rootView.getRootWindowInsets().getDisplayCutout();
//            }
//        }
//
//        if (!safeArea.equals(previousSafeArea)) {
//            previousSafeArea = new Rect(safeArea);
//            notifyWebAboutSafeAreaChanges(safeArea, displayCutout);
//        }
        JSObject ret = new JSObject();
        ret.put(KEY_INSET, safeAreaInsets.getSafeAreaInsets(this.getBridge()));
        notifyListeners("safeAreaChanged", ret);
    }


//    private void notifyWebAboutSafeAreaChanges(Rect safeArea, DisplayCutout displayCutout) {
//        int top = safeAreaInsets.dpToPixels(safeArea.top, bridge);
//        int bottom = 0;
//        int right = safeAreaInsets.dpToPixels(safeArea.right, bridge);
//        int left = safeAreaInsets.dpToPixels(safeArea.left, bridge);
//
//        if (displayCutout != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            // 如果有刘海屏或其他特殊屏幕形状，则获取真实的安全区域
//            top = safeAreaInsets.dpToPixels(displayCutout.getSafeInsetTop(), bridge);
//            left = safeAreaInsets.dpToPixels(displayCutout.getSafeInsetLeft(), bridge);
//            right = safeAreaInsets.dpToPixels(displayCutout.getSafeInsetRight(), bridge);
//            bottom = safeAreaInsets.dpToPixels(displayCutout.getSafeInsetBottom(), bridge);
//        }
//
//
//        JSObject data = new JSObject();
//        data.put(KEY_INSET, safeAreaInsets.result(top, left, right, bottom));
//        notifyListeners("safeAreaChanged", data);
//    }


    @PluginMethod
    public void getSafeAreaInsets(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put(KEY_INSET, safeAreaInsets.getSafeAreaInsets(this.getBridge()));
        call.resolve(ret);
    }

    @PluginMethod
    public void getStatusBarHeight(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put(Bar_Height, safeAreaInsets.getStatusBarHeight(this.getBridge()));
        call.resolve(ret);
    }
}
