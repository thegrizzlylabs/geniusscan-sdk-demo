import ProjectDescription

public func dependenciesAndPackages() -> ([TargetDependency], [Package]) {
    if case let .string(version) = Environment.sdkVersion, let sdkVersion = Version(string: version) {
        return (
            [ .package(product: "GSSDK") ],
            [.package(url: "https://github.com/thegrizzlylabs/geniusscan-sdk-spm", .upToNextMajor(from: sdkVersion))]
        )
    } else {
        return (
            [ .xcframework(path: "../../Frameworks/GSSDK.xcframework")],
            []
        )
    }
}
