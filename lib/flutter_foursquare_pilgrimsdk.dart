import 'dart:async';

import 'package:flutter/services.dart';

class FlutterFoursquarePilgrimsdk {
  static const MethodChannel _channel =
      const MethodChannel('flutter_foursquare_pilgrimsdk');

  static Future<void> get init async {
    await _channel.invokeMethod('init', {
      "clientId": "54G52IPKKBMW4HONX21OJX3BTWDHZN2TNYRLPDUGQXZVN3LZ",
      "clientSecret": "FC4E0AX1FJ0NUYFF01QOXXNPHQ4FJ5GN2ADTF1NEHXBZULYT",
    });
  }

  static Future<void> get start async {
    await _channel.invokeMethod('start');
  }

  static Future<String> get currentLocation async {
    return await _channel.invokeMethod('getCurrentLocation');
  }
}
