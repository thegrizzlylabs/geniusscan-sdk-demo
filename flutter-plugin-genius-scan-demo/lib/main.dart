import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_genius_scan/flutter_genius_scan.dart';

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
        body: MyScaffoldBody()
      ),
    );
  }
}

class MyScaffoldBody extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
        child: RaisedButton(
          onPressed: () {
            FlutterGeniusScan.scanWithConfiguration(
                {
                  'source': 'camera',
                  'multiPage': true,
                }
            ).then((result) {
              String pdfUrl = result['pdfUrl'];
              OpenFile.open(pdfUrl.replaceAll("file://", ''))
                  .then((result) => debugPrint(result),
                  onError: (error) => displayError(context, error)
              );
            },
                onError: (error) => displayError(context, error)
            );
          },
          child: Text("START SCANNING"),
        )
    );
  }

  void displayError(BuildContext context, PlatformException error) {
    Scaffold.of(context).showSnackBar(SnackBar(content: Text(error.message)));
  }
}
