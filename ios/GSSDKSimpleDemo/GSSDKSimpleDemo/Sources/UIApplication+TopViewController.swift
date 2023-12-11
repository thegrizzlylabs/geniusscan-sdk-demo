//
// Genius Scan SDK
//
// Copyright 2010-2023 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import Foundation
import UIKit

extension UIApplication {
    var topViewController: UIViewController? {
        let window = UIApplication.shared.keyWindow
        var topController = window?.rootViewController
        while let presentedController = topController?.presentedViewController {
            topController = presentedController
        }
        return topController
    }
}
