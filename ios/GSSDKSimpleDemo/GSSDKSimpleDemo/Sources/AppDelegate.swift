//
// Genius Scan SDK
//
// Copyright 2010-2025 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import GSSDK
import UIKit

class AppDelegate: NSObject, UIApplicationDelegate {
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {

        // Important: WITHOUT A LICENSE KEY, THE SDK WILL ONLY RUN FOR 60 SECONDS
        // - Obtain a free demo key on https://portal.geniusscansdk.com
        // - Make sure that your app's bundle ID matches the bundle ID of the key
        // GSK.setLicenseKey("<YOUR LICENSE KEY>")

        return true
    }
}
