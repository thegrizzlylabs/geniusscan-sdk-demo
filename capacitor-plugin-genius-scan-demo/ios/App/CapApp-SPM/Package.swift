// swift-tools-version: 5.9
import PackageDescription

// DO NOT MODIFY THIS FILE - managed by Capacitor CLI commands
let package = Package(
    name: "CapApp-SPM",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "CapApp-SPM",
            targets: ["CapApp-SPM"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", exact: "8.3.0"),
        .package(name: "CapawesomeTeamCapacitorFileOpener", path: "../../../node_modules/@capawesome-team/capacitor-file-opener"),
        .package(name: "ThegrizzlylabsCapacitorPluginGeniusScan", path: "../../../node_modules/@thegrizzlylabs/capacitor-plugin-genius-scan")
    ],
    targets: [
        .target(
            name: "CapApp-SPM",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm"),
                .product(name: "CapawesomeTeamCapacitorFileOpener", package: "CapawesomeTeamCapacitorFileOpener"),
                .product(name: "ThegrizzlylabsCapacitorPluginGeniusScan", package: "ThegrizzlylabsCapacitorPluginGeniusScan")
            ]
        )
    ]
)
