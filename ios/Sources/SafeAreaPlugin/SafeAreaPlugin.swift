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
        CAPPluginMethod(name: "setImmersiveNavigationBar", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "unsetImmersiveNavigationBar", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = SafeArea()
    
    private var orientationObserver: NSObjectProtocol?
    private var lastInsets: UIEdgeInsets?
    private var lastOrientation: UIInterfaceOrientation?
    private var pendingRotationTask: DispatchWorkItem?
    
    override public func load() {
        self.startListeningForSafeAreaChanges()
    }
    
    @objc private func startListeningForSafeAreaChanges() {
        guard orientationObserver == nil else { return }
        
        // 初始化：记录当前状态
        lastInsets = implementation.getSafeAreaInsets()
        lastOrientation = getCurrentOrientation()
        
        // iOS 13+ 使用设备方向变化通知，iOS 12- 使用状态栏方向变化
        if #available(iOS 13.0, *) {
            // 使用 UIDevice.orientationDidChangeNotification
            // 该通知在设备物理方向改变时触发，不会被废弃
            orientationObserver = NotificationCenter.default.addObserver(
                forName: UIDevice.orientationDidChangeNotification,
                object: nil,
                queue: .main
            ) { [weak self] _ in
                self?.handleOrientationChange()
            }
        } else {
            // iOS 12 及以下使用状态栏方向变化通知
            orientationObserver = NotificationCenter.default.addObserver(
                forName: UIApplication.didChangeStatusBarOrientationNotification,
                object: nil,
                queue: .main
            ) { [weak self] _ in
                self?.handleOrientationChange()
            }
        }
    }
    
    deinit {
        if let observer = orientationObserver {
            NotificationCenter.default.removeObserver(observer)
        }
        pendingRotationTask?.cancel()
    }
    
    private func getCurrentOrientation() -> UIInterfaceOrientation? {
        if #available(iOS 13.0, *) {
            // 尝试从多个来源获取正确的 window
            if let keyWindow = UIApplication.shared.windows.first(where: { $0.isKeyWindow }),
               let orientation = keyWindow.windowScene?.interfaceOrientation {
                return orientation
            }
            // 备选：从所有 window 中获取
            for window in UIApplication.shared.windows {
                if let orientation = window.windowScene?.interfaceOrientation {
                    return orientation
                }
            }
            return nil
        } else {
            return UIApplication.shared.statusBarOrientation
        }
    }
    
    private func handleOrientationChange() {
        // Debounce: 取消之前的待处理任务
        pendingRotationTask?.cancel()
        
        // 创建新的延迟任务
        let task = DispatchWorkItem { [weak self] in
            guard let self = self else { return }
            
            let currentOrientation = self.getCurrentOrientation()
            let currentInsets = self.implementation.getSafeAreaInsets()
            
            // 检查是否真正发生变化（方向改变 或 insets 改变）
            let orientationChanged = currentOrientation != self.lastOrientation
            let insetsChanged = !self.insetsEqual(self.lastInsets, currentInsets)
            
            if orientationChanged || insetsChanged {
                self.lastOrientation = currentOrientation
                self.lastInsets = currentInsets
                self.notifyWebAboutSafeAreaChanges(currentInsets)
            }
        }
        
        pendingRotationTask = task
        // 延迟 200ms，等待旋转动画完成和 safeAreaInsets 更新
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2, execute: task)
    }
    
    private func insetsEqual(_ insets1: UIEdgeInsets?, _ insets2: UIEdgeInsets) -> Bool {
        guard let insets1 = insets1 else { return false }
        return insets1.top == insets2.top &&
               insets1.bottom == insets2.bottom &&
               insets1.left == insets2.left &&
               insets1.right == insets2.right
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
        DispatchQueue.main.async { [weak self] in
            guard let self = self else {
                call.reject("Plugin instance not available")
                return
            }
            
            if #available(iOS 11.0, *) {
                let safeAreaInsets = self.implementation.getSafeAreaInsets()
                call.resolve([
                    "insets": [
                        "top": safeAreaInsets.top,
                        "bottom": safeAreaInsets.bottom,
                        "right": safeAreaInsets.right,
                        "left": safeAreaInsets.left,
                    ],
                ])
            } else {
                // iOS 11 以下没有安全区域，top 返回状态栏高度
                let barHeight = self.implementation.getStatusBarHeight()
                call.resolve([
                    "insets": [
                        "top": barHeight,
                        "bottom": 0,
                        "right": 0,
                        "left": 0,
                    ],
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
        if let observer = orientationObserver {
            NotificationCenter.default.removeObserver(observer)
            orientationObserver = nil
        }
        pendingRotationTask?.cancel()
        pendingRotationTask = nil
        call.resolve()
    }
    
    @objc func setImmersiveNavigationBar(_ call: CAPPluginCall) {
        call.resolve();
    }

    @objc func unsetImmersiveNavigationBar(_ call: CAPPluginCall) {
        call.resolve();
    }
}
