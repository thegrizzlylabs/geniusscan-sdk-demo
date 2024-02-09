//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import UIKit
import GSSDK

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

        // Set the SDK license key as early as possible to give it a chance to refresh
        // the license key in case it's expired (auto-refresh behavior can also be disabled with
        // setLicenseKey(_:autoRefresh:).

        // GSK.setLicenseKey("<YOUR LICENSE KEY - MAKE SURE TO CHANGE YOUR BUNDLE ID TO MATCH THE KEY - WITHOUT LICENSE KEY THE DEMO WILL RUN FOR ONLY 60 SECONDS>")

        let configuration = GSKCameraSessionConfiguration()

        let cameraSession = GSKCameraSession(configuration: configuration)
        window = UIWindow(frame: UIScreen.main.bounds)
        let cameraViewController = CameraViewController(cameraSession: cameraSession)!

        let navigationController = UINavigationController(rootViewController: cameraViewController)
        if #available(iOS 13.0, *) {
            let appearance = UINavigationBarAppearance()
            navigationController.navigationBar.standardAppearance = appearance
            navigationController.navigationBar.scrollEdgeAppearance = appearance
        }

        window?.rootViewController = navigationController
        window?.makeKeyAndVisible()

        return true
    }

}
