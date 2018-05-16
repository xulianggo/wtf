#import "ApiUiOpen.h"
#import "WtfTools.h"
#import "JSO.h"

@implementation ApiUiOpen

- (HybridHandler) getHandler{
    return ^(JSO *data, WtfCallback responseCallback) {
        WtfUi caller=self.currentUi;
        
        JSO *name=[data getChild:@"name"];
        NSString *name_s= [name toString];
        if([WtfTools isEmptyString:name_s]){
            //name_s=@"UiRoot";
            [WtfTools quickShowMsgMain:@"ApiUiOpen need @name" callback:^{
                //
            }];
            return;
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            WtfUi ui=[WtfTools startUi:name_s initData:data objCaller:caller];
            if(ui!=nil){
                [ui on:WtfHybridEventInitDone :^(NSString *eventName, JSO * extraData){
                    NSLog(@" init done!!!");
                }];
                
                [ui on:WtfHybridEventWhenClose :^(NSString *eventName, JSO * extraData){
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [caller resetTopBarStatus];
                        responseCallback(extraData);
                    });
                }];
            }
        });
    };
}

@end
