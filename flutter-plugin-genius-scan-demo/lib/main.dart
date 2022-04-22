import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_genius_scan/flutter_genius_scan.dart';
import 'package:path_provider/path_provider.dart';
import 'package:open_file/open_file.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
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
        child: ElevatedButton(
      onPressed: () {
        copyLanguageFile().then((folder) {
          FlutterGeniusScan.scanWithConfiguration({
            'source': 'camera',
            'multiPage': true,
            'ocrConfiguration': {
              'languages': ['eng'],
              'languagesDirectoryUrl': folder.path
            }
          }).then((result) {
            String documentUrl = result['multiPageDocumentUrl'];
            OpenFile.open(documentUrl.replaceAll("file://", '')).then(
                    (result) => debugPrint(result.message),
                onError: (error) => displayError(context, error));
          }, onError: (error) => displayError(context, error));
        });
      },
      child: Text("START SCANNING"),
    ));
  }

  Future<Directory> copyLanguageFile() async {
    Directory languageFolder = await getApplicationSupportDirectory();
    File languageFile = File(languageFolder.path + "/eng.traineddata");
    if (!languageFile.existsSync()) {
      ByteData data = await rootBundle.load("assets/eng.traineddata");
      List<int> bytes = data.buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);
      await languageFile.writeAsBytes(bytes);
    }
    return languageFolder;
  }

  void displayError(BuildContext context, PlatformException error) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(error.message!)));
  }
}
