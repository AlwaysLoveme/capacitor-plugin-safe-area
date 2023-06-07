package com.capacitor.safearea;

import android.util.Log;
import android.view.OrientationEventListener;
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

