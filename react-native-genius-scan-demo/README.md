# Genius Scan SDK for React Native demo

## Installation
If you don't have it already, install react native CLI
```
npm install -g react-native-cli
```

And then, from the demo folder:

Install project dependencies
```
npm install
```

Run the app in a simulator or on a phone
```
react-native run-ios
react-native run-android
```

## Development
When using this demo to develop the react-native plugin, it needs to load the plugin from the local `react-native-genius-scan` folder instead of the npm dependency.

Unfortunately [react native packager does not support symlinks](https://stackoverflow.com/a/47403470/1878592), so the local folder needs to be copied manually to the demo's 
node_modules directory after every local change. 

From demo folder, run:

```
cp -r ../react-native-genius-scan node_modules/\@thegrizzlylabs/
```