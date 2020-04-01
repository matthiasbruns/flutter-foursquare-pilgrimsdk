#import "FlutterFoursquarePilgrimsdkPlugin.h"
#if __has_include(<flutter_foursquare_pilgrimsdk/flutter_foursquare_pilgrimsdk-Swift.h>)
#import <flutter_foursquare_pilgrimsdk/flutter_foursquare_pilgrimsdk-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_foursquare_pilgrimsdk-Swift.h"
#endif

@implementation FlutterFoursquarePilgrimsdkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterFoursquarePilgrimsdkPlugin registerWithRegistrar:registrar];
}
@end
