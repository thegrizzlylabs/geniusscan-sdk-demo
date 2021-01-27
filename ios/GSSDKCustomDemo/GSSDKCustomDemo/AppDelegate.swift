//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//



import UIKit
import GSSDKCore

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        do {
            try GSK.initWithLicenseKey("<YOUR LICENSE KEY - MAKE SURE TO CHANGE YOUR BUNDLE ID TO MATCH THE KEY - WITHOUT LICENSE KEY THE DEMO WILL RUN FOR ONLY 60 SECONDS>")
        } catch {
            /**
             If the SDK is not properly initialized, the SDK method will return errors. This will help you setup the SDK properly. After that, the only reason why this may return an error would be if the license expires.
             All the SDK errors return proper errors in that case and you can handle them to ensure you provide a good "degraded" experience. For instance, you can prompt them to update the application to use the scanning
             feature.
             */
            print("Error while initializing the Genius Scan SDK: \(error)")
        }

        let configuration = GSKCameraSessionConfiguration()

        let cameraSession = GSKCameraSession(configuration: configuration)
        window = UIWindow(frame: UIScreen.main.bounds)
        let cameraViewController = CameraViewController(cameraSession: cameraSession)!

        let navigationController = UINavigationController(rootViewController: cameraViewController)
        window?.rootViewController = navigationController
        window?.makeKeyAndVisible()

        return true
    }

}
