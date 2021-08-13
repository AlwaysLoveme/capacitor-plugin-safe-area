package com.zhuxian.safearea;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "SafeArea")
public class SafeAreaPlugin extends Plugin {
    private SafeArea implementation;
    @Override
    public void load() {
        implementation = new SafeArea();
    }


    @PluginMethod
    public void getSafeAreaInsets(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("insets", implementation.getSafeAreaInsets(this.getBridge()));
        call.resolve(ret);
    }
}
