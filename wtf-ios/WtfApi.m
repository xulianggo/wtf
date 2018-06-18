#import "WtfApi.h"

@implementation WtfApi

- (WtfHandler) getHandler{
    return ^(JSO *ddd, WtfCallback responseCallback) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if(nil!=responseCallback) responseCallback(ddd);
        });
    };
}

@end
