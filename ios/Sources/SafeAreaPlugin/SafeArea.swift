import UIKit
import Foundation

@objc public class SafeArea: NSObject {
    
    // 多种方式获取当前活跃的 window，确保兼容性
    @objc public func getWindow() -> UIWindow? {
        // 方式1: iOS 15+ 使用 connectedScenes
        if #available(iOS 15.0, *) {
            let scenes = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
            
            // 优先获取激活状态的场景
            if let activeScene = scenes.first(where: { $0.activationState == .foregroundActive }) {
                if let keyWindow = activeScene.windows.first(where: { $0.isKeyWindow }) {
                    return keyWindow
                }
                // 备选：第一个非隐藏窗口
                if let visibleWindow = activeScene.windows.first(where: { !$0.isHidden }) {
                    return visibleWindow
                }
            }
            
            // 备选：任意场景的 keyWindow
            for scene in scenes {
                if let keyWindow = scene.windows.first(where: { $0.isKeyWindow }) {
                    return keyWindow
                }
            }
        }
        
        // 方式2: iOS 13-14 使用 windows 数组
        if #available(iOS 13.0, *) {
            // 优先从所有 window 中查找 keyWindow
            if let keyWindow = UIApplication.shared.windows.first(where: { $0.isKeyWindow }) {
                return keyWindow
            }
            
            // 备选1：查找激活的 window
            if let activeWindow = UIApplication.shared.windows.first(where: { 
                $0.windowLevel == .normal && !$0.isHidden 
            }) {
                return activeWindow
            }
            
            // 备选2：第一个可见窗口
            if let visibleWindow = UIApplication.shared.windows.first(where: { !$0.isHidden }) {
                return visibleWindow
            }
            
            // 最终备选：第一个窗口
            return UIApplication.shared.windows.first
        }
        
        // 方式3: iOS 12 及以下使用已废弃但仍可用的 keyWindow
        return UIApplication.shared.keyWindow
    }
    
    @objc public func getSafeAreaInsets() -> UIEdgeInsets {
        // 必须在主线程执行
        if !Thread.isMainThread {
            var insets: UIEdgeInsets = .zero
            DispatchQueue.main.sync {
                insets = self.getSafeAreaInsetsOnMainThread()
            }
            return insets
        }
        
        return getSafeAreaInsetsOnMainThread()
    }
    
    private func getSafeAreaInsetsOnMainThread() -> UIEdgeInsets {
        // iOS 11+ 才有 safeAreaInsets
        guard #available(iOS 11.0, *) else {
            // iOS 11 以下没有安全区域概念，返回状态栏高度作为 top
            let statusBarHeight = getStatusBarHeight()
            return UIEdgeInsets(top: statusBarHeight, left: 0, bottom: 0, right: 0)
        }
        
        // 尝试多种方式获取 safeAreaInsets
        
        // 方式1: 从 window 获取（最可靠）
        if let window = getWindow() {
            let insets = window.safeAreaInsets
            // 验证 insets 是否有效（至少有一个值不为0，或者全为0也是有效的）
            if insets.top >= 0 && insets.bottom >= 0 && insets.left >= 0 && insets.right >= 0 {
                return insets
            }
        }
        
        // 方式2: 从所有 windows 中查找有效的 safeAreaInsets
        if #available(iOS 13.0, *) {
            for window in UIApplication.shared.windows where !window.isHidden {
                let insets = window.safeAreaInsets
                // 如果找到非零的 insets，使用它
                if insets.top > 0 || insets.bottom > 0 || insets.left > 0 || insets.right > 0 {
                    return insets
                }
            }
        }
        
        // 方式3: 从根视图控制器的 view 获取
        if let window = getWindow(),
           let rootViewController = window.rootViewController {
            let insets = rootViewController.view.safeAreaInsets
            if insets.top >= 0 && insets.bottom >= 0 && insets.left >= 0 && insets.right >= 0 {
                return insets
            }
        }
        
        // 方式4: 尝试从第一个可用的 window 获取
        if #available(iOS 13.0, *) {
            if let firstWindow = UIApplication.shared.windows.first {
                return firstWindow.safeAreaInsets
            }
        } else {
            if let keyWindow = UIApplication.shared.keyWindow {
                return keyWindow.safeAreaInsets
            }
        }
        
        // 最终备选：返回零值
        return .zero
    }
    
    @objc public func getStatusBarHeight() -> CGFloat {
        // 必须在主线程执行
        if !Thread.isMainThread {
            var height: CGFloat = 0
            DispatchQueue.main.sync {
                height = self.getStatusBarHeightOnMainThread()
            }
            return height
        }
        
        return getStatusBarHeightOnMainThread()
    }
    
    private func getStatusBarHeightOnMainThread() -> CGFloat {
        var statusBarHeight: CGFloat = 0
        
        // iOS 13+ 从 WindowScene 获取
        if #available(iOS 13.0, *) {
            // 尝试从激活状态的场景获取
            let activeScene = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .first { $0.activationState == .foregroundActive }
            
            if let statusBarManager = activeScene?.statusBarManager {
                statusBarHeight = statusBarManager.statusBarFrame.size.height
            } else {
                // 备选：从第一个可用场景获取
                let firstScene = UIApplication.shared.connectedScenes
                    .compactMap { $0 as? UIWindowScene }
                    .first
                
                if let statusBarManager = firstScene?.statusBarManager {
                    statusBarHeight = statusBarManager.statusBarFrame.size.height
                }
            }
            
            // 如果还是获取不到，尝试从 window 的 safeAreaInsets 推断
            if statusBarHeight == 0, let window = getWindow() {
                if #available(iOS 11.0, *) {
                    statusBarHeight = window.safeAreaInsets.top
                }
            }
        } else {
            // iOS 12 及以下从 UIApplication 获取
            statusBarHeight = UIApplication.shared.statusBarFrame.size.height
        }
        
        // 验证返回值的合理性（一般在 20-60 之间）
        // 如果异常，尝试使用默认值
        if statusBarHeight < 0 || statusBarHeight > 100 {
            // iPhone X 系列刘海屏通常是 44pt，普通屏幕通常是 20pt
            if #available(iOS 11.0, *), let window = getWindow() {
                let topInset = window.safeAreaInsets.top
                if topInset > 20 {
                    return topInset // 刘海屏设备
                }
            }
            return 20 // 默认状态栏高度
        }
        
        return statusBarHeight
    }
}
