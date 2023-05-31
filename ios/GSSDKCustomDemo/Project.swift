import ProjectDescription
import ProjectDescriptionHelpers

let project = Project(
    name: "GSSDKCustomDemo",
    organizationName: "The Grizzly Labs",
    targets: [
        .sdkTarget(
            name: "CustomDemo",
            product: .app,
            additionalPlistEntries: [
                "NSCameraUsageDescription": "The Genius Scan SDK Custom Demo uses the camera for scanning documents.",
                "UILaunchStoryboardName": "LaunchScreen"
            ],
            sources: [
                "GSSDKCustomDemo/Sources/**"
            ],
            resources: [
                "GSSDKCustomDemo/Resources/**",
                .folderReference(path: "GSSDKCustomDemo/ResourceBundles/tessdata")
            ],
            dependencies: [
                .project(target: "Core", path: "../GSSDK"),
                .project(target: "OCR", path: "../GSSDK"),
            ]
        )
    ],
    schemes: [
        Scheme(
            name: "GSSDKCustomDemo",
            buildAction: .buildAction(targets: ["CustomDemo"])
        )
    ]
)
