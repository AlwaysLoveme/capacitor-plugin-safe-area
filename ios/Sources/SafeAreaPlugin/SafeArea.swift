import UIKit
import Foundation

@objc public class SafeArea: NSObject {
    @objc public func getWindow() -> UIWindow? {
        if #available(iOS 13, *) {
            return UIApplication.shared.windows.first { $0.isKeyWindow }
        } else {
            return UIApplication.shared.keyWindow
        }
    }
    
    
    @objc public func getSafeAreaInsets() -> UIEdgeInsets {
        let window: UIWindow? = self.getWindow();
        if #available(iOS 11.0, *) {
            return window?.safeAreaInsets ?? .zero
        }
        return .zero
    }
    
    @objc public func getStatusBarHeight() -> CGFloat {
        var statusBarHeight: CGFloat = 0
        if #available(iOS 13.0, *) {
            let scene = UIApplication.shared.connectedScenes.first
            guard let windowScene = scene as? UIWindowScene else { return 0 }
            guard let statusBarManager = windowScene.statusBarManager else { return 0 }
            statusBarHeight = statusBarManager.statusBarFrame.size.height
        } else {
            statusBarHeight = UIApplication.shared.statusBarFrame.size.height
        }
        return statusBarHeight
    }
}
