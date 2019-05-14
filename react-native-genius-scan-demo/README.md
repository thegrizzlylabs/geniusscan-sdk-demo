# Genius Scan SDK for React Native demo
This is a demo app for the [`@thegrizzlylabs/react-native-genius-scan`](https://www.npmjs.com/package/@thegrizzlylabs/react-native-genius-scan) npm package

## Installation
If you don't have it already, install `react native CLI`:
```
npm install -g react-native-cli
```

Install project dependencies:
```
npm install
```

Run the app in a simulator or on a smartphone:
```
react-native run-ios
react-native run-android
```

## Building and deployment

### iOS

To run the demo on a physical device, you first need to [sign code it in XCode](https://help.apple.com/xcode/mac/current/#/dev5a825a1ca), which requires a change of its Bundle ID:

1. Connect the device to your Mac and unlock it.
2. Open `/geniusscan-sdk-demo/react-native-genius-scan-demo/ios/demo.xcodeproj`in XCode
3. Choose your device from the XCode Scheme menu
4. Set your own [Bundle ID](https://help.apple.com/xcode/mac/current/#/dev9b66ae7df) under Identity
5. Enable [Automatic Signing](https://help.apple.com/xcode/mac/current/#/dev80cc24546) and select a Team you are a member of (personal or organization)
6. Press _Build and run the current scheme_

## Licensing

The Genius Scan SDK and plugins only work for **one minute per session** in trial mode. To test the scanner without restarting the demo application every minute, you must initialize it with a valid license key.

You can obtain free, 30-day Evalution Licenses from [our website](https://www.thegrizzlylabs.com/document-scanner-sdk/) by submitting the demo request form. Make sure to indicate a valid [Bundle ID](https://help.apple.com/xcode/mac/current/#/dev9b66ae7df).