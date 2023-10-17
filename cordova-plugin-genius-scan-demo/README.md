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

## Building and deployment

If you have trouble deploying your application after upgrading the plugin to a newer version, try removing the `node_modules/` `platforms/` and `plugins/` directories and `package.json` file from your project before running `cordova prepare` and `cordova run <platform>` again.

### iOS

To run the demo on a physical device, you first need to [sign code it in XCode](https://help.apple.com/xcode/mac/current/#/dev5a825a1ca), which requires a change of its Bundle ID:

1. Connect the device to your Mac and unlock it.
2. Open `/cordova-plugin-genius-scan-demo/platforms/ios/GS\ SDK\ Cordova\ Demo.xcworkspace` in XCode
3. Choose your device from the XCode Scheme menu
4. Set your own [Bundle ID](https://help.apple.com/xcode/mac/current/#/dev9b66ae7df) under Identity
5. Enable [Automatic Signing](https://help.apple.com/xcode/mac/current/#/dev80cc24546) and select a Team you are a member of (personal or organization)
6. Press _Build and run the current scheme_

## Licensing

The Genius Scan SDK and plugins only work for **one minute per session** in trial mode. To test the scanner without restarting the demo application every minute, you must initialize it with a valid license key.

You can obtain free, 30-day Evalution Licenses from [our website](https://geniusscansdk.com) by submitting the demo request form. Make sure to indicate a valid [Bundle ID](https://help.apple.com/xcode/mac/current/#/dev9b66ae7df).
