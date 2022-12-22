package com.zhuxian.safearea;

import android.util.Log;

import android.os.Build;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;

import android.view.WindowInsets;

public class SafeArea {

    public JSObject getSafeAreaInsets(Bridge bridge) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(SafeAreaPlugin.class.toString(), String.format("Requires at least %d+", Build.VERSION_CODES.M));
            return this.result(0, 0, 0, 0);
        }
        WindowInsets windowInsets = bridge.getActivity().getWindow().getDecorView().getRootWindowInsets();
        if (windowInsets == null) {
            Log.i(SafeAreaPlugin.class.toString(), "WindowInsets is not available.");
            return this.result(0, 0, 0, 0);
        }
        float density = this.getDensity(bridge);
        int top = Math.round(windowInsets.getStableInsetTop() / density);
        int left = Math.round(windowInsets.getStableInsetLeft() / density);
        int right = Math.round(windowInsets.getStableInsetRight() / density);
        int bottom = Math.round(windowInsets.getStableInsetBottom() / density);

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
