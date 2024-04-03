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

class AppDelegate: NSObject, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        // Set the SDK license key as early as possible to give it a chance to refresh
        // the license key in case it's expired (auto-refresh behavior can also be disabled with
        // setLicenseKey(_:autoRefresh:).

        // GSK.setLicenseKey("<YOUR LICENSE KEY - MAKE SURE TO CHANGE YOUR BUNDLE ID TO MATCH THE KEY - WITHOUT LICENSE KEY THE DEMO WILL RUN FOR ONLY 60 SECONDS>")

        return true
    }

}
