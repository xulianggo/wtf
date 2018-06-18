#import "ApiLangChange.h"


#import "WtfTools.h"
#import "JSO.h"

@implementation ApiLangChange

- (WtfHandler) getHandler
{
    return ^(JSO * jso, WtfCallback responseCallback) {
        
        NSString *lang = [[jso getChild:@"lang"] toString];
        if(![WtfTools isEmptyString:lang]){
            [WtfTools setLang:lang];
        }
        
    };
}

@end
