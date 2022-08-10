import Foundation

@objc public class SafeArea: NSObject {
    @objc public func getSafeAreaInsets(_ value: String) -> String {
        return value
    }

    @objc public func getWindow() -> UIWindow? {
        if #available(iOS 13, *) {
            return UIApplication.shared.windows.first { $0.isKeyWindow }
        } else {
            return UIApplication.shared.keyWindow
        }
    }
}
