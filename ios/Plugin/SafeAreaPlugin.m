#import <Capacitor/Capacitor.h>
#import <Foundation/Foundation.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(SafeAreaPlugin, "SafeArea",
           CAP_PLUGIN_METHOD(getSafeAreaInsets, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getStatusBarHeight, CAPPluginReturnPromise);
           )
