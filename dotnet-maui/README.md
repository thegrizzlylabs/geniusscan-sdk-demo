# Genius Scan SDK for .NET MAUI demo

This is a .NET MAUI demo app showcasing the [`GeniusScanSDK.ScanFlow`](https://www.nuget.org/packages/GeniusScanSDK.ScanFlow/) package. It demonstrates how to:

- start the document ScanFlow
- scan barcodes
- generate a PDF from the captured pages

## Requirements

- .NET SDK 9 minimum
- The .NET MAUI workload
- Xcode for iOS builds
- Android SDK / Android Studio for Android builds

## Installation

From this folder:

1. Restore the MAUI workload and NuGet dependencies

   ```bash
   dotnet workload restore
   dotnet restore SimpleDemo.csproj
   ```

2. Build the app for your target platform:

   ```bash
   dotnet build SimpleDemo.csproj -f net10.0-ios
   dotnet build SimpleDemo.csproj -f net10.0-android
   ```

You can then run the `SimpleDemo` project from your IDE on an iOS simulator/device or Android emulator/device.

## Licensing

The SDK works for **one minute per session** in trial mode. To avoid that limit, set a valid license key in [MainPage.xaml.cs](MainPage.xaml.cs).
You can obtain free, 30-day Evaluation Licenses from [our website](https://geniusscansdk.com) by creating an account on our customer portal.
