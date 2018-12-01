
# Genius Scan SDK Cordova plugin demo

This is a demo app for the [`@thegrizzlylabs/cordova-plugin-genius-scan`](https://www.npmjs.com/package/@thegrizzlylabs/cordova-plugin-genius-scan) npm package

## Requirements

For the demo app installation you will require:
* [Cordova](https://cordova.apache.org/#getstarted), minimum version 7.1.0

## Installation

1. Install platforms and plugins according to `config.xml`
    * `cordova prepare`

2. Build and run the project
    * `cordova run ios` (or check [Cordova iOS documentation](https://cordova.apache.org/docs/en/latest/guide/platforms/ios/index.html))
    * `cordova run android` (or check [Cordova Android documentation](https://cordova.apache.org/docs/en/latest/guide/platforms/android/index.html))

**Xcode 10**: [Cordova-ios is not fully compatible with Xcode 10 yet](https://github.com/apache/cordova-ios/issues/407), you may need to use `cordova run ios --buildFlag="-UseModernBuildSystem=0"` command to build the project properly