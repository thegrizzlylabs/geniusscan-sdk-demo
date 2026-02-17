# Demo Apps

The sample apps available in our [demo repository](https://github.com/thegrizzlylabs/geniusscan-sdk-demo/tree/master/ios) demonstrate how to integrate the SDK.

## Simple Demo

The simple demo demonstrates the ``GSKScanFlow`` integration that lets you integrate a scanning module in your app with just a few lines of code. Customization of the scan flow is possible with ``GSKScanFlowConfiguration``.

It also demonstrates the ``GSKBarcodeScanFlow`` integration.

## Custom Demo

The custom demo demonstrates how to build an entirely custom scanning experience by relying on lower-level components such as our ``GSKCameraViewController`` (a view controller that lets you scan a document), ``GSKEditFrameViewController`` (a view controller that lets you edit the cropping of a document) and ``GSKScanProcessor`` which lets you process scans as you want.

The custom demo also shows the usage of ``GSKOCR`` if you want to perform text recognition on your scans.

## Licensing

The Genius Scan SDK and plugins only work for **one minute per session** in trial mode. To test the scanner without restarting the demo application every minute, you must initialize it with a valid license key.

You can obtain free, 30-day Evaluation Licenses from [our website](https://geniusscansdk.com).
