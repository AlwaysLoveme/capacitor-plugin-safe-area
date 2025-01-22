import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(SafeAreaPlugin)
public class SafeAreaPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "SafeAreaPlugin"
    public let jsName = "SafeArea"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getSafeAreaInsets", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getStatusBarHeight", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "setImmersiveNavigationBar", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = SafeArea()

    private var observer: NSObjectProtocol?

    override public func load() {
        self.startListeningForSafeAreaChanges()
    }

    // 监听安全区域的变化，通过监听状态栏变化
    @objc private func startListeningForSafeAreaChanges() {
        if observer == nil {
            if #available(iOS 13.0, *) {
                observer = NotificationCenter.default.addObserver(forName: UIDevice.orientationDidChangeNotification, object: nil, queue: nil) { [weak self] _ in
                    guard let self = self else { return }
                    self.handleSafeAreaChange()
                }
            } else {
                observer = NotificationCenter.default.addObserver(forName: UIApplication.didChangeStatusBarOrientationNotification, object: nil, queue: nil) { [weak self] _ in
                    guard let self = self else { return }
                    self.handleSafeAreaChange()
                }
            }
        }
    }

    deinit {
        if let observer = observer {
            NotificationCenter.default.removeObserver(observer)
            self.observer = nil
        }
    }

    private func handleSafeAreaChange() {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            let safeAreaInsets = self.implementation.getSafeAreaInsets()
            self.notifyWebAboutSafeAreaChanges(safeAreaInsets)
        }
    }

    private func notifyWebAboutSafeAreaChanges(_ safeAreaInsets: UIEdgeInsets) {
        let data: [String: Any] = [
            "insets": [
                "top": safeAreaInsets.top,
                "bottom": safeAreaInsets.bottom,
                "right": safeAreaInsets.right,
                "left": safeAreaInsets.left
            ]
        ]
        self.notifyListeners("safeAreaChanged", data: data)
    }

    @objc func getSafeAreaInsets(_ call: CAPPluginCall) {
        var top: CGFloat = 0.00,
            bottom: CGFloat = 0.00,
            right: CGFloat = 0.00,
            left: CGFloat = 0.00
        if #available(iOS 11.0, *) {
            DispatchQueue.main.async {
                let safeAreaInsets: UIEdgeInsets = self.implementation.getSafeAreaInsets()
                top = safeAreaInsets.top
                right = safeAreaInsets.right
                bottom = safeAreaInsets.bottom
                left = safeAreaInsets.left
                call.resolve([
                    "insets": [
                        "top": top,
                        "bottom": bottom,
                        "right": right,
                        "left": left
                    ]
                ])
            }
        } else {
            // 没有安全区域的设备，top 返回值为状态栏高度
            DispatchQueue.main.async {
                let barHeight = self.implementation.getStatusBarHeight()
                call.resolve([
                    "insets": [
                        "top": barHeight,
                        "bottom": bottom,
                        "right": right,
                        "left": left
                    ]
                ])
            }
        }
    }

    @objc func getStatusBarHeight(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            let barHeight = self.implementation.getStatusBarHeight()
            call.resolve(["statusBarHeight": barHeight])
        }
    }

    @objc func stopListeningForSafeAreaChanges(_ call: CAPPluginCall) {
        if let observer = observer {
            NotificationCenter.default.removeObserver(observer)
            self.observer = nil
        }
        call.resolve()
    }

    @objc func setImmersiveNavigationBar(_ call: CAPPluginCall) {
        call.resolve()
    }
}
