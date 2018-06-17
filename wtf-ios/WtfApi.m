#import "WtfApi.h"

@implementation WtfApi

//please overrided by decendants

//-(void) handler:(JSO*)ddd :(WtfCallback)responseCallback
//{
//    dispatch_async(dispatch_get_main_queue(), ^{
//        //self.currentUi.responseData=ddd;
//        //[self.currentUi closeUi];
//        if(nil!=responseCallback)
//            responseCallback(ddd);
//    });
//}

- (WtfHandler) getHandler{
    return ^(JSO *ddd, WtfCallback responseCallback) {
        dispatch_async(dispatch_get_main_queue(), ^{
            //self.currentUi.responseData=ddd;
            //[self.currentUi closeUi];
            if(nil!=responseCallback) responseCallback(ddd);
        });
    };
}

@end
