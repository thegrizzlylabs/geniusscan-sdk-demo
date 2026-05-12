# Genius Scan Capacitor Demo

This project is a sample Capacitor application showing how to integrate and use `@thegrizzlylabs/capacitor-plugin-genius-scan`.

## Requirements

- Node.js
- Capacitor 8
- Xcode for iOS
- Android Studio and Java 21 for Android

## Installation

```bash
npm install
npx cap sync
```

## Run the demo

Open the native projects:

- `npx cap open ios`
- `npx cap open android`

Or build directly from the command line:

- `npx cap run ios`
- `npx cap run android`

## License key

The SDK works for 60 seconds per session without a license key.

To test for longer or to use the plugin in production, initialize it with a valid license key in the app startup code:

```typescript
await GeniusScan.setLicenseKey('<your license key>');
```

Evaluation licenses are available from [geniusscansdk.com](https://geniusscansdk.com).
