#import <UIKit/UIKit.h>
#import <sys/utsname.h>
#import "WtfTools.h"

#import <CommonCrypto/CommonDigest.h>

@implementation WtfTools

SINGLETON_shareInstance(WtfTools);

+(void) setLang :(NSString *)lang
{
    [WtfTools shareInstance].lang=lang;
}
//+ (void)checkAppConfig{
//
//    WtfTools *theWtfTool = [self shareInstance];
//    if(nil==theWtfTool._jAppConfig){
//        //TODO [WtfTools stripComment:s]
//        NSString *s =[self readAssetInStr:@"config.json" :YES];
//        if(s){
//            theWtfTool._jAppConfig = [JSO s2o:s];
//            theWtfTool._i18n =[theWtfTool._jAppConfig getChild:@"I18N"];
//        }
//    }
//    //TODO if not ok, need to alert the program ...
//}

+ (void) startUi :(NSString *)strUiName
         initData:(JSO *) initData
        objCaller:(WtfUi )objCaller
         //callback:(void (^)(WtfUi ui))callback
         callback:(WtfUiCallback)callback
{
    WtfUi  ui = [self startUi:strUiName initData:initData objCaller:objCaller];
    if(nil!=callback){
        callback(ui);
    }
}

+ (WtfUi ) startUi :(NSString *)strUiName
              initData:(JSO *) initData
             objCaller:(WtfUi)objCaller
{
    //[self checkAppConfig];
    
    JSO *jso_uiMapping = [self getAppConfig:@"ui_mapping"];
    
    JSO *uiConfig = [[jso_uiMapping getChild:strUiName] copy];//important to copy one otherwise the real one will be poluted
    
    NSString *mode = [JSO o2s:[uiConfig getChild:@"mode"]];
    NSString *className = [JSO o2s:[uiConfig getChild:@"class"]];
    
    if ( [self isEmptyString :className]) {
        if( [@"WebView" isEqualToString:mode]){
            if([WtfTools os_compare:8.0]>=0)
            {
                className=@"WtfWKWebViewUi";//default to this now.
            }else{
                className=@"WtfWebViewUi";//WARNING for <iOS8, using UIWebView, which slower and using private api
            }
        }else{
            return nil;
        }
    }
    
    Class uiClass = NSClassFromString(className);
    WtfUi theWtfUi = [[uiClass alloc] init];
    
    if (nil==theWtfUi) {
        [self quickShowMsgMain:[NSString stringWithFormat:@"Not found %@ %@ to startUi", strUiName, className]];
        return nil;
    }
    
    [uiConfig basicMerge:initData];
    theWtfUi.uiName=strUiName;
    theWtfUi.uiData=uiConfig;
    
    //theWtfUi.responseData=[JSO id2o:@{}];
    
    /////////////////////////////////////// Display It {
    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
    if (ddd.window.rootViewController==nil){
        
        if ([theWtfUi isKindOfClass:[UITabBarController class]]) {
            ddd.window.rootViewController = (UIViewController *)theWtfUi;
        }
        else{
            UINavigationController *nav
            = [[UINavigationController alloc] initWithRootViewController:(UIViewController *)theWtfUi];
            ddd.window.rootViewController = nav;
        }
    }
    
    UIViewController *ui = (UIViewController *)theWtfUi;
    
    //preload, which will call viewDidLoad first.
    [ui.view layoutSubviews];
    
    if (objCaller == nil) {
    }
    else{
        if (((UIViewController *)objCaller).navigationController != nil) {
            //for test only...((UIViewController *)theWtfUi).view.backgroundColor=[UIColor brownColor];
            [((UIViewController *)objCaller).navigationController pushViewController:ui animated:YES];
        }
        else{
            // modal
            [(UIViewController *)objCaller presentViewController:ui animated:YES completion:nil];
        }
    }
    /////////////////////////////////////// Display It }
    return theWtfUi;
}

+ (WtfApi *)getHybridApi:(NSString *)name{
    
    Class myApiClass = NSClassFromString(name);
    
    id myApiClassInstance = [[myApiClass alloc] init];
    
    if (myApiClassInstance) {
        // NSLog(@"返回api的是：(%@)", myApiClassInstance);
        return myApiClassInstance;
    }
    else{
        [self quickShowMsgMain:[NSString stringWithFormat:@"Api: %@ not found", name]];
    }
    
    return nil;
}

+ (JSO *)wholeAppConfig{
    
    WtfTools *theWtfTool = [self shareInstance];
    if(nil==theWtfTool._jAppConfig){
        //TODO [WtfTools stripComment:s]
        NSString *s =[self readAssetInStr:@"config.json" :YES];
        if(s){
            theWtfTool._jAppConfig = [JSO s2o:s];
            theWtfTool._i18n =[theWtfTool._jAppConfig getChild:@"I18N"];
        }
    }
    return theWtfTool._jAppConfig;
}

+ (JSO *) getAppConfig :(NSString *)key
{
    return [[self wholeAppConfig] getChild:key];
}

+ (void)appAlert:(NSString *)msg callback:(WtfBlock)callback
{
    //dispatch_async fix :
    //This application is modifying the autolayout engine from a background thread, which can lead to engine corruption and weird crashes.
    //This will cause an exception in a future release
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:msg message:@"" preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:(void (^ __nullable)(UIAlertAction *action))callback];
        [alertController addAction:ok];
        
        //modal it
        [[self findTopRootView] presentViewController:alertController animated:NO completion:^(){
            //
        }];
    });
}
+ (void)appAlert:(NSString *)msg
{
    [self appAlert:msg callback:^(){
        NSLog(@" completion after appAlert()");
    }];
}

//TODO make it as a hint ... as what android did???!!!
+ (void)quickShowMsgMain:(NSString *)msg{

    [self quickShowMsgMain:msg callback:^(){
        NSLog(@" completion after quickShowMsgMain()");
    }];
}

+ (void)quickShowMsgMain:(NSString *)msg callback:(WtfBlock)callback
{
    //dispatch_async fix :
    //This application is modifying the autolayout engine from a background thread, which can lead to engine corruption and weird crashes.
    //This will cause an exception in a future release
    dispatch_async(dispatch_get_main_queue(), ^{
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:msg message:@"" preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:(void (^ __nullable)(UIAlertAction *action))callback];
        [alertController addAction:ok];
        
        //modal it
        [[self findTopRootView] presentViewController:alertController animated:NO completion:^(){
            //
        }];
    });
}

+ (UIViewController *) findTopRootView
{
    UIViewController *topRootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
    while (topRootViewController.presentedViewController)
    {
        topRootViewController = topRootViewController.presentedViewController;
    }
    
    return topRootViewController;
}

//IOS 8 +
+ (void)appConfirm:(NSString *)msg
                 handlerYes:(WtfDialogCallback) handlerYes
                  handlerNo:(WtfDialogCallback) handlerNo
{
    
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:msg message:@"" preferredStyle:UIAlertControllerStyleAlert];
    
    [alertController addAction:[UIAlertAction actionWithTitle:@"Yes" style:UIAlertActionStyleDefault handler:handlerYes]];
    [alertController addAction:[UIAlertAction actionWithTitle:@"No" style:UIAlertActionStyleDefault handler:handlerNo]];
    
    [[self findTopRootView] presentViewController:alertController animated:NO completion:nil];
}

+ (void) suspendApp
{
    //home button press programmatically
    UIApplication *app = [UIApplication sharedApplication];
    NSLog(@"Hide...");
    //[app performSelector:@selector(suspend)];
    [self call_o_do_m:app :@"suspend"];
}

+ (void) KillAppSelf
{
    [self suspendApp];
    
    sleep(2);
    
    NSLog(@"quitGracefully() after suspendApp");
    
    exit(EXIT_SUCCESS);
}

+ (void) call_o_do_m :(id)ooo :(NSString *)mmm
{
    SEL sel = NSSelectorFromString(mmm);
    if ([(id)ooo respondsToSelector:sel]) {
        ((void (*)(id, SEL))[ooo methodForSelector:sel])(ooo, sel);
    }
}

//+ (void) call_c_do_m :(NSString *)ccc :(NSString *)mmm
//{
//    Class cls = NSClassFromString(ccc);
//    SEL sel = NSSelectorFromString(mmm);
//
//    if ([(id)cls respondsToSelector:sel]) {
//        ((void (*)(id, SEL))[cls methodForSelector:sel])(cls, sel);
//        //((void (*)(id, SEL, NSString *))[cls methodForSelector:sss])(cls, sss, @"local");
//    }
//}
+ (void) call_c_do_m :(NSString *)ccc :(NSString *)mmm :(NSString *) vvv
{
    Class cls = NSClassFromString(ccc);
    SEL sel = NSSelectorFromString(mmm);
    
    if ([(id)cls respondsToSelector:sel]) {
        ((void (*)(id, SEL, NSString *))[cls methodForSelector:sel])(cls, sel, vvv);
    }
}

+(NSString *) fullPathOfAsset :(NSString *) filename
{
    NSString *rt
    = [[NSBundle mainBundle]
       pathForResource:[filename stringByDeletingPathExtension]
       ofType:[filename pathExtension]];
    return rt;
}

+(NSString *)readAssetInStr :(NSString *)filename
{
    return [NSString
            stringWithContentsOfFile:[self fullPathOfAsset:filename]
            encoding:NSUTF8StringEncoding
            error:NULL];
}

+(NSString *)readAssetInStr :(NSString *)filename :(BOOL)removeComments
{
    NSString *rt=[self readAssetInStr:filename];
    if(nil==rt)return nil;
    rt=[self quickRegExpReplace :@"^[ \t]*//.*$" :rt :@""];
    return rt;
}

+(BOOL) isEmptyString :(NSString *)s
{
    return (nil==s || [@"" isEqualToString:s]);
}

//+(NSArray *) quickRegExpMatchLine :(NSString *)regex_s :(NSString *)txt
//{
//    NSError *error = NULL;
//    NSRange range = NSMakeRange(0, [txt length]);
//    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:regex_s options:NSRegularExpressionAnchorsMatchLines error:&error];
//    if(nil!=error){
//        NSLog(@"error when quickRegExpMatch %@",error);
//    }
//    return [regex matchesInString:txt options:0 range:range];
//}

+(NSArray *) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt
{
    NSError *error = NULL;
    NSRange range = NSMakeRange(0, [txt length]);
    NSRegularExpression *regex =
    [NSRegularExpression regularExpressionWithPattern:regex_s
                                              options:0
                                                error:&error];
    if(nil!=error){
        NSLog(@"error when quickRegExpMatch %@",error);
    }
    return [regex matchesInString:txt options:0 range:range];
}

+(NSString *) quickRegExpReplace :(NSString *)regex_s :(NSString *)src :(NSString *)tgt
{
    NSError *error = NULL;
    NSRange range = NSMakeRange(0, [src length]);
    NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:regex_s options:NSRegularExpressionAnchorsMatchLines error:&error];
    if(nil!=error){
        NSLog(@"error when quickRegExpReplace %@",error);
    }
    return [regex stringByReplacingMatchesInString:src options:0 range:range withTemplate:tgt];
}

+ (void) countDown:(double)interval initTime:(double)initTime block:(BOOL (^)(NSTimer *tm))block
{
    if(initTime<=0){
        return;
    }
    __block double countTime=initTime;
    
    __block NSTimer * ttt=[NSTimer scheduledTimerWithTimeInterval:interval target:[NSBlockOperation blockOperationWithBlock:^(){
        countTime=countTime-interval;
        if(countTime<=0){
            [ttt invalidate];
            return;
        }
        BOOL rt = block(ttt);
        if(rt==YES){
            NSLog(@".");
            [ttt invalidate];
            countTime=0;
        }
    }] selector:@selector(main) userInfo:nil repeats:YES];
}

+ (NSString *) I18N:(NSString *)key
{
    //JSO *_i18n=[self getAppConfig:@"I18N"];
    //    JSO *_i18n = [self wholeAppConfig]._i18n;
    WtfTools *theWtfTool = [self shareInstance];
    JSO *_i18n =theWtfTool._i18n;
    
    JSO *value_a=[_i18n getChild:key];
    //NSString *cached_lang=[self loadAppConfig:@"lang"];
    NSString *lang=theWtfTool.lang;
    if([self isEmptyString:lang]){
        lang=@"en";
    }
    JSO *value=[value_a getChild:lang];
    if(nil==value || [value isNull]){
        //
    }else{
        return [value toString];
    }
    return key;
}

+ (NSInteger) os_compare:(Float32)tgt
{
    float sysver=[[[UIDevice currentDevice] systemVersion] floatValue];
    if(sysver>tgt)return 1;
    if(sysver<tgt)return -1;
    return 0;
}
+ (BOOL) is_simulator
{
    struct utsname systemInfo;
    uname(&systemInfo);
    
    NSString * tgt= [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
    
    if([tgt isEqualToString:@"i386"]) return YES;
    if([tgt isEqualToString:@"x86_64"]) return YES;
    
    NSString *name = [[UIDevice currentDevice] name];
    if ([name hasSuffix:@"Simulator"]) {
        return YES;
    }
    NSLog(@"is_simulator tgt = %@",tgt);
    return NO;
}

+ (NSString *) base64encode:(NSString *)s { return [[s dataUsingEncoding:NSUTF8StringEncoding] base64EncodedStringWithOptions:0];}
+ (NSString *) base64decode:(NSString *)s { return [[NSString alloc] initWithData:[[NSData alloc] initWithBase64EncodedString:s options:0] encoding:NSUTF8StringEncoding];}

//TODO maybe support save app config in future? no, don't think so.
//+ (void)saveAppConfig
//{
//
//    WtfTools *theWtfTool = [self shareInstance];
//    NSString *jsonString = [JSO o2s:theWtfTool.jso];
//
////    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
////    [userDefaults setObject:jsonString forKey:@"appConfig"];
////    [userDefaults synchronize];
//    [self saveUserConfig:@"appConfig" :jsonString :true];
//}
//

//+ (JSO *)loadAppConfig
//{
//
//    //    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
//    //    NSString *jsonString =[userDefaults objectForKey:@"appConfig"];
//    NSString *jsonString = (NSString *) [self loadUserConfig:@"appConfig"];
//
//    JSO *jsonJso = [JSO s2o:jsonString];
//
//    return jsonJso;
//}

//@ref use in app_cache_save
//+ (void)saveUserConfig :(NSString *)key :(NSString *)value_s :(BOOL)autosave
//{
//    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
//    [userDefaults setObject:value_s forKey:key];
//    if(autosave){
//        [userDefaults synchronize];
//    }
//}

+ (void)saveUserConfig :(NSString *)key :(JSO *)jso //:(BOOL)autosave
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    if(nil==jso)[userDefaults removeObjectForKey:key];
    else [userDefaults setObject:[jso toString] forKey:key];
    //if(autosave){
        [userDefaults synchronize];
    //}
}

//@ref use in app_cache_load
//+ (nullable id)loadUserConfig :(NSString *)key
//{
//    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
//    return [userDefaults objectForKey:key];
//}

//@ref use in app_cache_load
+ (JSO *)loadUserConfig :(NSString *)key
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString * jso_s = [userDefaults objectForKey:key];
    if(nil==jso_s)return nil;//patch
    return [JSO s2o:jso_s];
}

+ (NSString *) getBuildType
{
    NSString * build_type = @"L";//default Live...
#ifdef DEBUG
    build_type = @"D";//DEBUG for "Run" in XCode
#else
    BOOL hasEmbeddedMobileProvision = !![[NSBundle mainBundle] pathForResource:@"embedded" ofType:@"mobileprovision"];
    if (hasEmbeddedMobileProvision)
    {
        build_type = @"M";//DEMO for TestFlight
    } else {
        build_type = @"L";//LIVE for real app store
    }
#endif
    return build_type;
}

//forward event(Pause) to uiRoot to handle
+ (void) notifyPause
{
    WtfTools *theWtfTool = [self shareInstance];
    if(nil!=theWtfTool.uiRoot){
        [theWtfTool.uiRoot trigger:WtfEventAppPause];
    }
}

//forward event(Resume) to uiRoot to handle
+ (void) notifyResume
{
    WtfTools *theWtfTool = [self shareInstance];
    if(nil!=theWtfTool.uiRoot){
        [theWtfTool.uiRoot trigger:WtfEventAppResume];
    }
}

//close/dismiss the root ui
+ (void) finishRoot
{
    id<UIApplicationDelegate> ddd = [UIApplication sharedApplication].delegate;
    if (ddd.window.rootViewController){
        [ddd.window.rootViewController dismissViewControllerAnimated:YES completion:^{
            NSLog(@"WtfTool.finishRoot() => dismissViewControllerAnimated");
        }];
        ddd.window.rootViewController=nil;
    }
}

+ (NSString *) md5 :(NSString*) ipt
{
    
    const char * pointer = [ipt UTF8String];
    unsigned char md5Buffer[CC_MD5_DIGEST_LENGTH];
    
    CC_MD5(pointer, (CC_LONG)strlen(pointer), md5Buffer);
    
    NSMutableString *string = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_MD5_DIGEST_LENGTH; i++)
        [string appendFormat:@"%02x",md5Buffer[i]];
    return string;
}


////////////////////////

+(instancetype) on:(NSString *)eventName :(WtfEventHandler) handler
{
    WtfTools *theWtfTool = [WtfTools shareInstance];
    [WtfTools on:eventName :handler :nil];
    return theWtfTool;
}

+(instancetype) on:(NSString *)eventName :(WtfEventHandler) handler :(JSO *)initData
{
    WtfTools *theWtfTool = [WtfTools shareInstance];
    if(nil==handler){
        return theWtfTool;
    }
    if(nil==theWtfTool.eventHandlers){
        theWtfTool.eventHandlers=[[WtfCache new]init];
    }
    if(nil==[theWtfTool.eventHandlers objectForKey:eventName]){
        [theWtfTool.eventHandlers setObject:[NSMutableArray array] forKey:eventName];
    }
    [[theWtfTool.eventHandlers objectForKey:eventName] addObject:handler];
    return theWtfTool;
}

+(instancetype) on:(NSString *)eventName :(WtfEventHandler) handler :(JSO *)initData :(NSInteger)expire
{
    WtfTools *theWtfTool = [WtfTools shareInstance];
    if(nil==handler){
        return theWtfTool;
    }
    if(nil==theWtfTool.eventHandlers){
        theWtfTool.eventHandlers=[[WtfCache new]init];
    }
    if(nil==[theWtfTool.eventHandlers objectForKey:eventName]){
        [theWtfTool.eventHandlers setObject:[NSMutableArray array] forKey:eventName expire:expire];
    }
    NSMutableArray * dict = [theWtfTool.eventHandlers objectForKey:eventName];
    [dict addObject:handler];
    return theWtfTool;
}

+(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData
{
    NSLog(@"trigger(%@) is called.", eventName);
    WtfTools *theWtfTool = [WtfTools shareInstance];
    NSArray * dict = [theWtfTool.eventHandlers objectForKey:eventName];
    if(nil!=dict){
        NSUInteger c =[dict count];
        for(int i=0; i<c; i++){
            WtfEventHandler hdl=[dict objectAtIndex:i];
            if(nil!=hdl){
                if(nil==triggerData) triggerData=[JSO id2o:@{}];
                NSLog(@"with triggerData %@", [triggerData toString]);
                hdl(eventName, triggerData);
            }
        }
    }
    return theWtfTool;
}

//remove the link to the handler only....
+(instancetype) off :(NSString *)eventName :(WtfEventHandler) handler
{
    WtfTools *theWtfTool = [WtfTools shareInstance];
    if(nil==theWtfTool.eventHandlers || [@"*" isEqualToString:eventName]){
        //self.eventHandlers=[NSMutableDictionary dictionary];
        theWtfTool.eventHandlers=[[WtfCache new]init];
    }
    //[self.eventHandlers[eventName] removeObject:handler];
    [[theWtfTool.eventHandlers objectForKey:eventName] removeObject:handler];
    return theWtfTool;
}
//remove all handlers linked to the eventName
+(instancetype) off :(NSString *)eventName
{
    WtfTools *theWtfTool = [WtfTools shareInstance];
    //TODO: potentially memory leak, fix later (find a removeAll or free() stuffs!!
    if(nil==theWtfTool.eventHandlers || [@"*" isEqualToString:eventName]){
        //self.eventHandlers=[NSMutableDictionary dictionary];
        theWtfTool.eventHandlers=[[WtfCache new]init];
    }
    [theWtfTool.eventHandlers setObject:[NSMutableArray array] forKey:eventName];
    return theWtfTool;
}

+(instancetype) trigger :(NSString *)eventName
{
    return [WtfTools trigger:eventName :nil];
}
@end
