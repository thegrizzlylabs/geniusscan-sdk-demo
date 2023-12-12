import ProjectDescription

let project = Project(
    name: "GSSDKCustomDemo",
    organizationName: "The Grizzly Labs",
    targets: [
        Target(
            name: "GSSDKCustomDemo",
            platform: .iOS,
            product: .app,
            productName: "GSSDKCustomDemo",
            bundleId: "com.thegrizzlylabs.$(PRODUCT_NAME:rfc1034identifier)",
            deploymentTarget: .iOS(targetVersion: "13.0", devices: [.ipad, .iphone]),
            infoPlist: .extendingDefault(with:
                [
                    "NSCameraUsageDescription": "The Genius Scan SDK Simple Demo uses the camera for scanning documents.",
                    "NSLibraryUsageDescription": "The Genius Scan SDK Custom Demo uses the photo library for importing documents.",
                    "UILaunchStoryboardName": "LaunchScreen"
                ]
            ),
            sources: [
                "GSSDKCustomDemo/Sources/**"
            ],
            resources: [
                "GSSDKCustomDemo/Resources/**",
                .folderReference(path: "GSSDKCustomDemo/ResourceBundles/tessdata")
            ]
        )
    ],
    schemes: [
        Scheme(
            name: "GSSDKCustomDemo",
            buildAction: .buildAction(targets: ["GSSDKCustomDemo"])
        )
    ]
)
