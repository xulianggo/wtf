#import "ApiUiTitle.h"
#import "JSO.h"

@interface ApiUiTitle ()

@end

@implementation ApiUiTitle

- (HybridHandler) getHandler
{
    return ^(JSO * jso, WtfCallback responseCallback) {
        
        NSString *titlename = [[jso getChild:@"title"] toString];
        
        WtfUi caller=self.currentUi;
        
        [caller setTopBarTitle:titlename];
    };
}

@end
