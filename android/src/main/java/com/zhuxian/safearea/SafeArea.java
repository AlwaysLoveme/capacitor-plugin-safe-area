package com.zhuxian.safearea;

import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;

public class SafeArea {

    public JSObject getSafeAreaInsets(Bridge bridge) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Log.i(SafeAreaPlugin.class.toString(), String.format("Requires at least %d+", Build.VERSION_CODES.P));
            return this.result(0, 0, 0, 0);
        }

        WindowInsets windowInsets = bridge.getActivity().getWindow().getDecorView().getRootWindowInsets();
        if (windowInsets == null) {
            Log.i(SafeAreaPlugin.class.toString(), "WindowInsets is not available.");
            return this.result(0, 0, 0, 0);
        }
        float density = this.getDensity(bridge);

        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if (displayCutout == null) {
            Log.i(SafeAreaPlugin.class.toString(), "DisplayCutout is not available.");
            int top = Math.round(windowInsets.getStableInsetTop() / density);
            int left = Math.round(windowInsets.getStableInsetLeft() / density);
            int right = Math.round(windowInsets.getStableInsetRight() / density);
            int bottom = Math.round(windowInsets.getStableInsetBottom() / density);
            return this.result(top, left, right, bottom);
        }
        int top = Math.round(displayCutout.getSafeInsetTop() / density);
        int left = Math.round(displayCutout.getSafeInsetLeft() / density);
        int right = Math.round(displayCutout.getSafeInsetRight() / density);
        int bottom = Math.round(displayCutout.getSafeInsetBottom() / density);

        return this.result(top, left, right, bottom);
    }

    public int getStatusBarHeight(Bridge bridge) {
        int top = 0;
        float density = this.getDensity(bridge);
        int resourceId = bridge.getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            top = bridge.getActivity().getResources().getDimensionPixelSize(resourceId);
        }
        return Math.round(top / density);
    }

    private JSObject result(int top, int left, int right, int bottom) {
        JSObject json = new JSObject();
        json.put("top", top);
        json.put("left", left);
        json.put("right", right);
        json.put("bottom", bottom);
        return json;
    }

    private float getDensity(Bridge bridge) {
        return bridge.getActivity().getResources().getDisplayMetrics().density;
    }
}
