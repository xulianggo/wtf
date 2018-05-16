#import "WtfHybridApi.h"

@implementation WtfHybridApi

//please overrided by decendants

- (HybridHandler) getHandler{
    return ^(JSO *ddd, WtfCallback responseCallback) {
        dispatch_async(dispatch_get_main_queue(), ^{
            //self.currentUi.responseData=ddd;
            //[self.currentUi closeUi];
            if(nil!=responseCallback)
                responseCallback(ddd);
        });
    };
}

@end
