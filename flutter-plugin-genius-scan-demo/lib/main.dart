import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_genius_scan/flutter_genius_scan.dart';
import 'package:path_provider/path_provider.dart';
import 'package:open_filex/open_filex.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {

    // This code shows how to initialize the SDK with a license key.
    // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
    //
    // FlutterGeniusScan.setLicenseKey('<Your license key>');

    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('GS SDK Flutter Demo'),
          ),
          body: MyScaffoldBody()),
    );
  }
}

class MyScaffoldBody extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () async {
                try {
                  // Start scan flow
                  var scanConfiguration = {
                    'source': 'camera',
                    'multiPage': true,
                    'ocrConfiguration': {
                      'languages': ['en-US']
                    }
                  };
                  var scanResult = await FlutterGeniusScan.scanWithConfiguration(scanConfiguration);
                  debugPrint('scanResult: $scanResult');

                  // Here is how you can display the resulting document:
                  String documentUrl = scanResult['multiPageDocumentUrl'];
                  await OpenFilex.open(documentUrl.replaceAll("file://", ''));

                  // You can also generate your document separately from selected pages:
                  /*
                  var appFolder = await getApplicationDocumentsDirectory();
                  var documentUrl = appFolder.path + '/mydocument.pdf';
                  var document = {
                    'pages': [{
                      'imageUrl': scanResult['scans'][0]['enhancedUrl'] ,
                      'hocrTextLayout': scanResult['scans'][0]['ocrResult']['hocrTextLayout']
                    }]
                  };
                  var documentGenerationConfiguration = { 'outputFileUrl': documentUrl };
                  await FlutterGeniusScan.generateDocument(document, documentGenerationConfiguration);
                  await OpenFilex.open(documentUrl);
                  */

                } on PlatformException catch (error) {
                  displayError(context, error);
                }
              },
              child: Text("Scan documents"),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                try {
                  // Start readable code scanning
                  var readableCodeConfiguration = {
                    'isBatchModeEnabled': true,
                    'supportedCodeTypes': ['qr', 'code128', 'ean13']
                  };
                  var result = await FlutterGeniusScan.scanReadableCodesWithConfiguration(readableCodeConfiguration);
                  debugPrint('readableCodeResult: $result');

                  // Display the detected codes
                  List<dynamic> codes = result['readableCodes'];
                  String codesText = codes.map((code) => '${code['type']}: ${code['value']}').join('\n');

                  showDialog(
                    context: context,
                    builder: (BuildContext context) {
                      return AlertDialog(
                        title: Text('Detected Codes'),
                        content: Text(codesText.isEmpty ? 'No codes detected' : codesText),
                        actions: [
                          TextButton(
                            onPressed: () => Navigator.of(context).pop(),
                            child: Text('OK'),
                          ),
                        ],
                      );
                    },
                  );

                } on PlatformException catch (error) {
                  displayError(context, error);
                }
              },
              child: Text("Scan barcodes"),
            ),
          ],
        ));
  }

  void displayError(BuildContext context, PlatformException error) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(error.message!)));
  }
}
