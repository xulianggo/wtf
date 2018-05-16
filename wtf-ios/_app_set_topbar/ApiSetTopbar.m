#import "JSO.h"
#import "WtfTools.h"
#import "ApiSetTopbar.h"

@implementation ApiSetTopbar

- (HybridHandler) getHandler
{
    return ^(JSO * jso, WtfCallback responseCallback) {
        
        WtfUi caller=self.currentUi;
        
        JSO *topbarmode= [jso getChild :@"mode"];
        JSO *save= [jso getChild :@"save"];
        NSString *save_s=[save toString];
        if([WtfTools isEmptyString:save_s])
        {
            save_s=@"Y";
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            if([@"Y" isEqualToString:save_s])
            {
                //save
                JSO *uidata =caller.uiData;
                [uidata setChild:@"topbar" JSO:topbarmode];
                [caller resetTopBarStatus];
            }else{
                NSString *topbarmode_s=[JSO o2s:topbarmode];
                [caller resetTopBar :topbarmode_s];
            }
            //responseCallback([JSO s2o:[JSO id2s:@{@"STS":@"OK"} :YES]]);
            responseCallback([JSO id2o:@{@"STS":@"OK"}]);
        });
    };
}

@end
