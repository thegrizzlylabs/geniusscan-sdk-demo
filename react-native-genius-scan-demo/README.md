# Genius Scan SDK for React Native demo

## Installation

1. Ensure [you are using the recommended version of Node](https://reactnative.dev/docs/environment-setup?guide=native). For instance, for React Native 0.71, you need to use Node 14 or newer.

    ```
    nvm install 14
    nvm alias default 14
    ```

2. If you don't have it already, install the React Native CLI:

    ```
    npm install -g react-native-cli
    ```

3. Install the project dependencies:

    ```
    cd demo
    npm install
    ```

4. For iOS, install the Cocoapods dependencies:

    ```
    cd ios
    pod install
    ```

5. Run the app:

    ```
    npx react-native run-ios
    npx react-native run-android
    ```

    For iOS you can open the `ios/demo.xcworkspace` project:

    ```
    xed ios/demo.xcworkspace
    ```