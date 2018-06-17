#import "ApiUiClose.h"
#import "JSO.h"

@implementation ApiUiClose

- (WtfHandler) getHandler{
    return ^(JSO *jso, WtfCallback responseCallback) {
        dispatch_async(dispatch_get_main_queue(), ^{
            
            WtfUi ui = self.currentUi;
//[ui setResponseData:jso]
//self.currentUi.responseData=jso;
//[ui closeUi];
            //[ui trigger:WtfEventWhenClose];
            
            [ui finishUi];
            [ui trigger:WtfEventWhenClose :jso];//@ need the startUi to handle the close event?
            
            //use responseCallback
            if(responseCallback)
            responseCallback([JSO id2o:@{@"STS":@"OK"}]);
        });
    };
}

@end
