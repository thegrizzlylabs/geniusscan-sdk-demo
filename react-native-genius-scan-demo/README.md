# Genius Scan SDK for React Native demo

## Installation

1. Ensure [you are using a Node version supported by React Native](https://reactnative.dev/docs/environment-setup?guide=native). React Native 0.87 requires Node 22.13 or newer.

```
nvm install 22
nvm alias default 22
```

2. Install the project dependencies:

```
yarn install
```

3. Run the app:

```
yarn ios
yarn android
```

The `yarn ios` command sets up the Swift Package Manager integration before building the app.

For iOS, it's also possible to run the app from Xcode. Set up the Swift Package Manager integration first:

```
yarn spm:setup
```

Then start Metro in a separate terminal:

```
yarn start
```

Then open the project and run the `demo` scheme:

```
xed ios/demo.xcodeproj
```
