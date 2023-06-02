import ProjectDescription

let project = Project(
    name: "GSSDKSimpleDemo",
    organizationName: "The Grizzly Labs",
    targets: [
        Target(
            name: "GSSDKSimpleDemo",
            platform: .iOS,
            product: .app,
            productName: "GSSDKSimpleDemo",
            bundleId: "com.thegrizzlylabs.$(PRODUCT_NAME:rfc1034identifier)",
            deploymentTarget: .iOS(targetVersion: "11.0", devices: [.ipad, .iphone]),
            infoPlist: .extendingDefault(with:
                [
                    "UIMainStoryboardFile": "Main",
                    "NSCameraUsageDescription": "The Genius Scan SDK Simple Demo uses the camera for scanning documents.",
                    "NSLibraryUsageDescription": "The Genius Scan SDK Simple Demo uses the photo library for importing documents.",
                    "UILaunchStoryboardName": "LaunchScreen"
                ]
            ),
            sources: [
                "GSSDKSimpleDemo/Sources/**"
            ],
            resources: [
                "GSSDKSimpleDemo/Resources/**",
                .folderReference(path: "GSSDKSimpleDemo/ResourceBundles/tessdata")
            ]
        )
    ],
    schemes: [
        Scheme(
            name: "GSSDKSimpleDemo",
            buildAction: .buildAction(targets: ["GSSDKSimpleDemo"])
        )
    ]
)
