package com.zhuxian.safearea;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;

public class SafeArea {

    public JSObject getSafeAreaInsets(Bridge bridge) {
        int top = 0, bottom = 0, left = 0, right = 0;
        float density = bridge.getActivity().getResources().getDisplayMetrics().density;
        int resourceId = bridge.getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            top = bridge.getActivity().getResources().getDimensionPixelSize(resourceId);
        }
        JSObject json = new JSObject();
        json.put("top", top / density);
        json.put("left", left);
        json.put("right", right);
        json.put("bottom", bottom);
        return json;
    }
}
