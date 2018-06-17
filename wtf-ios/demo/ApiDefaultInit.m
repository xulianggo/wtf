#import "ApiDefaultInit.h"
#import "WtfTools.h"
@implementation ApiDefaultInit
- (WtfHandler) getHandler{
    return ^(JSO *ddd, WtfCallback responseCallback) {
        //dispatch_async(dispatch_get_main_queue(), ^{
        
        //[self.currentUi closeUi];
        //TODO from ddd => real printer
        JSO *rt=[[JSO alloc] init];
        NSString *myapp_s = [WtfTools readAssetInStr:@"web/app/myapp.js" :YES];
        
        [rt setChild:@"myapp_s" JSO:[JSO id2o:myapp_s]];
        
        NSString * build_type=[WtfTools getBuildType];
        
        NSBundle * mainBundle =[NSBundle mainBundle];
        NSString *code = [mainBundle objectForInfoDictionaryKey:@"CFBundleShortVersionString"];//CFBundleVersion = build version
        NSString *version = [mainBundle objectForInfoDictionaryKey:@"CFBundleVersion"];//CFBundleVersion = build version
        
        
        //build_type=@"L";//local test only, don't submit this line...
        
        [rt setChild:@"build_type" JSO:[JSO id2o:build_type]];
        
        [rt setChild:@"build_version" JSO:[JSO id2o:version]];
        [rt setChild:@"build_code" JSO:[JSO id2o:code]];
        
        //NSLog(@"return jso=%@",[jso toString :true]);
        responseCallback(rt);
        //});
    };
}
@end
