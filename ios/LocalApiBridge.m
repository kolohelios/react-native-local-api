#import "RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(LocalApi, NSObject)

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

RCT_EXTERN_METHOD(
  apiRequest:(NSString *) url
  method:(NSString *) method
  params:(NSDictionary *) params
  body:(NSDictionary *) body
  setCookie:(BOOL) setCookie
  resolver:(RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)

RCT_EXTERN_METHOD(clearCookies)

RCT_EXTERN_METHOD(
  setTimeout:(double) timeout
)

@end
