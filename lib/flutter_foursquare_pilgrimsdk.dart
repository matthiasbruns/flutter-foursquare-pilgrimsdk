import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class FlutterFoursquarePilgrimsdk {
  static const MethodChannel _channel =
      const MethodChannel('flutter_foursquare_pilgrimsdk');

  static Future<void> init(
      {@required String clientId, @required String}) async {
    await _channel.invokeMethod('init', {
      "clientId": clientId,
      "clientSecret": clientSecret,
    });
  }

  static Future<void> get start async {
    await _channel.invokeMethod('start');
  }

  static Future<String> get currentLocation async {
    return await _channel.invokeMethod('getCurrentLocation');
  }
}
