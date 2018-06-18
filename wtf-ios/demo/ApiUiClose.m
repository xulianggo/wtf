#import "ApiUiClose.h"
#import "JSO.h"

@implementation ApiUiClose

- (WtfHandler) getHandler{
    return ^(JSO *jso, WtfCallback responseCallback) {
        dispatch_async(dispatch_get_main_queue(), ^{
            
            WtfUi ui = self.currentUi;
            
            [ui finishUi];
            
            //use responseCallback
            if(responseCallback)
            responseCallback([JSO id2o:@{@"STS":@"OK"}]);
        });
    };
}

@end
