#import "ApiLangChange.h"


#import "WtfTools.h"
#import "JSO.h"

@implementation ApiLangChange

- (HybridHandler) getHandler
{
    return ^(JSO * jso, WtfCallback responseCallback) {
        
        NSString *lang = [[jso getChild:@"lang"] toString];
        if(![WtfTools isEmptyString:lang]){
            [WtfTools shareInstance].lang=lang;
        }
        
    };
}

@end
