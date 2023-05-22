package com.capacitor.safearea;

import android.view.ViewTreeObserver;
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

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener;
    private boolean isListening = false;


    @Override
    public void load() {
        safeAreaInsets.setBridge(this.getBridge());
        this.startListeningForSafeAreaChanges();
    }

    private void startListeningForSafeAreaChanges() {
        if (!isListening) {
            FrameLayout rootView = bridge.getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

            layoutListener = this::detectSafeAreaChanges;
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

            isListening = true;
        }
    }

    @PluginMethod
    public void startListeningForSafeAreaChanges(PluginCall call) {
        this.startListeningForSafeAreaChanges();
        call.resolve();
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

