import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_foursquare_pilgrimsdk/flutter_foursquare_pilgrimsdk.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool _sdkInitialized = false;
  bool _sdkStarted = false;
  bool _loading = false;

  String _sdkStatusMessage = "Initializing SDK";

  @override
  void initState() {
    super.initState();
    _initPilgrimSDK();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> _initPilgrimSDK() async {
    _sdkInitialized = false;

    try {
      await FlutterFoursquarePilgrimsdk.init(
        clientId: "54G52IPKKBMW4HONX21OJX3BTWDHZN2TNYRLPDUGQXZVN3LZ",
        clientSecret: "FC4E0AX1FJ0NUYFF01QOXXNPHQ4FJ5GN2ADTF1NEHXBZULYT",
      );
      _sdkInitialized = true;
      _sdkStatusMessage = "SDK initialized";
    } catch (err) {
      _sdkInitialized = false;
      _sdkStatusMessage = "SDK init failed\n$err";
    }

    if (!mounted) return;

    setState(() {
      _sdkInitialized = _sdkInitialized;
      _sdkStatusMessage = _sdkStatusMessage;
    });
  }

  Future<void> _startPilgrimSDK() async {
    _sdkStarted = false;

    try {
      await FlutterFoursquarePilgrimsdk.start;
      _sdkStarted = true;
      _sdkStatusMessage = "SDK started";
    } catch (err) {
      _sdkStarted = false;
      _sdkStatusMessage = "SDK start failed\n$err";
    }

    if (!mounted) return;

    setState(() {
      _sdkStarted = _sdkStarted;
      _sdkStatusMessage = _sdkStatusMessage;
    });
  }

  Future<void> _getCurrentLocation() async {
    setState(() {
      _loading = true;
    });

    try {
      var location = await FlutterFoursquarePilgrimsdk.currentLocation;
      _sdkStatusMessage = "Current Location $location";
    } catch (err) {
      _sdkStatusMessage = "Location lookup failed\n$err";
    }

    if (!mounted) return;

    setState(() {
      _loading = false;
      _sdkStatusMessage = _sdkStatusMessage;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Foursquare Pilgim SDK Demo'),
        ),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              children: <Widget>[
                _loading ? CircularProgressIndicator() : Container(),
                Text(
                  '$_sdkStatusMessage\n',
                  textAlign: TextAlign.center,
                ),
                _sdkInitialized && !_sdkStarted
                    ? RaisedButton(
                        child: Text("Start Pilgrim SDK"),
                        onPressed: _startPilgrimSDK,
                      )
                    : Container(),
                _sdkInitialized && _sdkStarted
                    ? RaisedButton(
                        child: Text("Get current location"),
                        onPressed: _getCurrentLocation,
                      )
                    : Container(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
