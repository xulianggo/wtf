#import "WtfUi.h"

@interface WtfApi : NSObject

//TODO from android setCallerUi()/getCallerUi()


@property (nonatomic, weak) WtfUi currentUi;

//TODO see if merge with android handler()
- (HybridHandler) getHandler;
//e.g.
//{
//    return ^(JSO *ddd, WtfCallback responseCallback) {
//        //dispatch_async(dispatch_get_main_queue(), ^{
//        responseCallback([JSO id2o:@{@"STS":@"TODO"}]);
//        //}
//    };

@end
