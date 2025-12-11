//
//  Created by Bruno Virlet on 5/7/19.
//  Copyright © 2019 The Grizzly Labs. All rights reserved.
//

import Foundation
import GSSDK
import UIKit
import SwiftUI

@main
@available(iOS 14.0, *)
struct GSKSDKStructuredDemoApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            MainView()
        }
    }
}
