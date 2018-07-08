#import "WtfWKWebViewUi.h"

#import "WtfTools.h"

@interface WKScriptMessageHandler : NSObject  <WKScriptMessageHandler>

@property (weak) WtfUi caller;

+(instancetype) initWithWtfUi:(WtfUi) ui;

@end

@implementation WKScriptMessageHandler
+(instancetype) initWithWtfUi:(WtfUi) ui
{
    WKScriptMessageHandler *idid = [[self alloc] init];
    idid.caller=ui;
    return idid;
}

- (void) callWebViewDoJs:(id) _webview :(NSString *)js_s
{
    @try {
        [_webview evaluateJavaScript:js_s completionHandler:^(id _Nullable val, NSError * _Nullable error) {
            //code
        }];
    } @catch (NSException *exception) {
        NSLog(@"WKWebView evaluateJavaScript error %@", exception);
    } @finally {
        
    }
}
//---- protocol <WKScriptMessageHandler> ----
//TODO need to improve that if prepare so much thing for every msg posted, the performance is very bad !!!
- (void)userContentController: (WKUserContentController *)userContentController
      didReceiveScriptMessage:(WKScriptMessage *)message{
    
    WtfUi caller=_caller;
    
    JSO * msg=[JSO id2o:message.body];
    NSString * handlerName_s = [[msg getChild:@"handlerName"] toString];
    if([WtfTools isEmptyString:handlerName_s]){
        NSLog(@"Unknow Message from WKWebview: %@", message.body);
        return;
    }
    __block NSString * callBackId_s =[[msg getChild:@"callbackId"] toString];
    JSO * param =[msg getChild:@"data"];
    WKWebView *webView=message.webView;
    
    //to check the handlerName is auth by api_auth in config.json for current url
    
    JSO * api_auth = [WtfTools getAppConfig:@"api_auth"];
    NSString * uiname = caller.uiName;
    JSO * api_auth_a = [api_auth getChild:uiname];
    if(nil==api_auth_a){
        NSLog(@" !!! find no api_auth for uiname %@", uiname);
        return;
    }
    //NSString * handlerName_s = [handlerName toString];
    if([WtfTools isEmptyString:handlerName_s]){
        NSLog(@" empty handlerName?? %@", param);
        return;
    }
    BOOL flagFoundMatch=NO;
    NSMutableArray *found_a=[[NSMutableArray alloc] init];
    
    NSURL *url =[webView URL];
    NSString *scheme = [url scheme];
    NSString *fullurl =[url absoluteString];
    NSString *currenturl=fullurl;
    if( [@"file" isEqualToString:scheme]){
        currenturl=[url lastPathComponent];
    }
    for (NSString *kkk in [api_auth_a getChildKeys]) {
        if([currenturl isEqualToString:kkk]){
            flagFoundMatch=YES;
            //found_a= [api_auth_a getChild:kkk];
            //break;
            //[found_a basicMerge:[api_auth_a getChild:kkk]];
            JSO *jj =[api_auth_a getChild:kkk];
            id idjj = [jj toId];
            [found_a removeObjectsInArray:idjj];
            [found_a addObjectsFromArray:idjj];
        }
        
        if ([WtfTools quickRegExpMatch :kkk :fullurl]){
            flagFoundMatch=YES;
            //found_a= [api_auth_a getChild:kkk];
            //break;
            //[found_a basicMerge:[api_auth_a getChild:kkk]];
            JSO *jj =[api_auth_a getChild:kkk];
            id idjj = [jj toId];
            [found_a removeObjectsInArray:idjj];
            [found_a addObjectsFromArray:idjj];
        }
    }
    if(flagFoundMatch!=YES){
        NSLog(@" !!! find no auth for handlerName(%@) uiname(%@) url(%@)", handlerName_s, uiname, currenturl);
        return;
    }
    
    BOOL flagInList=NO;
    NSArray * keys =[found_a copy];
    for (NSString *vvv in keys){
        if([handlerName_s isEqualToString:vvv]){
            flagInList=YES;
            break;
        }
    }
    
    if (flagInList!=YES){
        NSLog(@" !!! handler %@ is not in auth list %@", handlerName_s, keys);
        return;
    }
    if(nil==caller.apiMap) {
        NSLog(@" !!! caller.myApiHandlers is nil !!! %@", caller.uiData);
        return;
    }
    
    WtfApi* api = caller.apiMap[handlerName_s];
    if (nil==api) {
        NSLog(@" !!! found no api for %@", handlerName_s);
        return;
    }
    WtfHandler handler = [api getHandler];
    if (nil==handler) {
        NSLog(@" !!! found no handler for %@", handlerName_s);
        return;
    }
    
    //NSString *callBackId_s=[callBackId toString];
    WtfCallback callback=^(JSO *responseData){
        //NSLog(@"WtfCallback responseData %@", [responseData toString]);
        NSString *rt_s=[JSO id2s:@{@"responseId":callBackId_s,@"responseData":[responseData toId]}];
        
        @try {
            NSString* javascriptCommand = [NSString stringWithFormat:@"setTimeout(function(){WebViewJavascriptBridge._app2js(%@);},1);", rt_s];
            //NOTES: very important to call back the js in the main q
            dispatch_async(dispatch_get_main_queue(), ^{
                //[WtfTools callWebViewDoJs:webView :javascriptCommand];
                [self callWebViewDoJs:webView :javascriptCommand];
            });
            //[caller evalJs:javascriptCommand];
        } @catch (NSException *exception) {
            NSLog(@" !!! error when callback to js %@",exception);
        } @finally {
        }
    };
    
    dispatch_after
    (dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.01 * NSEC_PER_SEC)),//async delay 0.01 second
     dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0),
     ^{
         @try {
             //like WtfHandler.onCall()
             handler([JSO s2o:[param toString]], callback);
         } @catch (NSException *exception) {
             callback([JSO id2o:@{@"STS":@"KO",@"errmsg":[exception reason]}]);
         }
     });
}

@end

//@ref
//https://github.com/jnjosh/PandoraBoy/
NSString *PBResourceHost = @"WtfWKWebViewUi";

@interface WtfResourceURLProtocol : NSURLProtocol {}

@end

@interface ResourceURL : NSURL {
}

+ (ResourceURL*) resourceURLWithPath:(NSString *)path;
@end

@implementation WtfResourceURLProtocol

+ (BOOL)canInitWithRequest:(NSURLRequest *)request
{
    BOOL flag1=[[[request URL] scheme] isEqualToString:@"local"];
    BOOL flag2=[[[request URL] host] isEqualToString:PBResourceHost];
    BOOL rt= (flag1 && flag2);
    return rt;
}

+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request
{
    return request;
}

-(void)startLoading
{
    NSBundle *thisBundle = [NSBundle bundleForClass:[self class]];
    //    NSString *abs_path = [thisBundle resourcePath];
    //    abs_path = [abs_path stringByAppendingString:@"/web/"];
    NSString *abs_path = [[thisBundle resourcePath] stringByAppendingString:@"/web/"];
    NSString *notifierPath = [abs_path stringByAppendingPathComponent:[[[self request] URL] path]];
    NSError *err;
    NSData *data = [NSData dataWithContentsOfFile:notifierPath
                                          options:NSUncachedRead
                                            error:&err];
    if( data )
    {
        NSURLResponse *response = [[NSURLResponse alloc] initWithURL:[[self request] URL]
                                                            MIMEType:@"text/html"
                                               expectedContentLength:[data length]
                                                    textEncodingName:nil];
        
        [[self client] URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageAllowed];
        [[self client] URLProtocol:self didLoadData:data];
        [[self client] URLProtocolDidFinishLoading:self];
    }
    else
    {
        NSLog(@"BUG:Unable to load resource:%@:%@", notifierPath, [err description]);
        [[self client] URLProtocol:self didFailWithError:err];
    }
}

-(void)stopLoading
{
    return;
}

@end

@implementation ResourceURL

+ (ResourceURL *) resourceURLWithPath:(NSString *)path
{
    NSURL *rt= [[NSURL alloc] initWithScheme:@"local"
                                        host:PBResourceHost
                                        path:path];
    return (ResourceURL *)rt;
}

@end

@implementation WtfWKWebViewUi

//TODO must make it as class private var, not global BOOL !!!!!!!!
BOOL isFirstLoad=YES;

+ (void) callWebViewLoadUrl:_webview :(NSString *)address
{
    if([WtfTools isEmptyString:address]) return;
    
    WKWebView *wv=_webview;
    @try {
        {
            NSURL *address_url = [NSURL URLWithString:address];
            NSString *scheme_s=[address_url scheme];
            
            if( [ WtfTools isEmptyString:scheme_s ])
            {
                //regard as local url
                ResourceURL *resource = [ResourceURL resourceURLWithPath:[@"/" stringByAppendingString:address]];
                //ResourceURL *resource = [ResourceURL resourceURLWithPath:[@"/web/" stringByAppendingString:address]];//force to web/
                [wv loadRequest:[NSURLRequest requestWithURL:resource]];
            }else{
                //[self loadUrl:[address_url absoluteString]];
                NSURL *requesturl = [NSURL URLWithString:[address_url absoluteString]];
                NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
                [wv loadRequest:request];
            }
        }
    } @catch (NSException *exception) {
        NSLog(@"WKWebView callWebViewLoadUrl error %@", exception);
    } @finally {
        
    }
}

//+ (void) callWebViewDoJs:(id) _webview :(NSString *)js_s
//{
//    @try {
//        [_webview evaluateJavaScript:js_s completionHandler:^(id _Nullable val, NSError * _Nullable error) {
//            //code
//        }];
//    } @catch (NSException *exception) {
//        NSLog(@"WKWebView evaluateJavaScript error %@", exception);
//    } @finally {
//
//    }
//}

- (void) webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation
{
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    
    [self spinnerOn];
}

- (void) webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation
{
    if (webView != self.myWebView) {
        NSLog(@" webViewDidStartLoad: not the same webview?? ");
        return;
    }
    isFirstLoad=NO;
    [self spinnerOff];
}

//Invoked when an error occurs while starting to load data for the main frame.
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@" webview didFailProvisionalNavigation for desc %@",[error description]);
    if(_myWebView==webView){
        
        if(isFirstLoad)
            [self finishUi];
        else
            [self spinnerOff];
    }
}

//Invoked when an error occurs during a committed main frame navigation.
- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@" webview didFailNavigation for desc %@",[error description]);
    if(_myWebView==webView)
        [self spinnerOff];
}

//----------------   <WKUIDelegate>   -----------------
- (void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler
{
    [WtfTools quickShowMsgMain:message callback:^{
        @try{
            if(completionHandler!=nil)
            completionHandler();
        } @catch (NSException *exception) {
            NSLog(@" error quickShowMsgMain %@", [exception reason]);
        }
    }];
}

- (void)webView:(WKWebView *)webView runJavaScriptConfirmPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(BOOL))completionHandler
{
    [WtfTools appConfirm:message handlerYes:^(UIAlertAction *action) {
        @try{
            if(completionHandler!=nil)
            completionHandler(YES);
        } @catch (NSException *exception) {
            NSLog(@" error quickShowMsgMain YES %@", [exception reason]);
        }
    } handlerNo:^(UIAlertAction *action) {
        @try{
            if(completionHandler!=nil)
            completionHandler(NO);
        } @catch (NSException *exception) {
            NSLog(@" error quickShowMsgMain NO %@", [exception reason]);
        }
    }];
}

- (void)webView:(WKWebView *)webView
runJavaScriptTextInputPanelWithPrompt:(NSString *)prompt
    defaultText:(NSString *)defaultText
initiatedByFrame:(WKFrameInfo *)frame
completionHandler:(void (^)(NSString * _Nullable))completionHandler
{
    //NSString *hostString = webView.URL.host;
    //NSString *sender = [NSString stringWithFormat:@"%@ からの表示", hostString];
    NSString *sender = @"";
    
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:prompt message:sender preferredStyle:UIAlertControllerStyleAlert];
    [alertController addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        //textField.placeholder = defaultText;
        textField.text = defaultText;
    }];
    [alertController addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
        NSString *input = ((UITextField *)alertController.textFields.firstObject).text;
        completionHandler(input);
    }]];
    [alertController addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
        completionHandler(nil);
    }]];
    [self presentViewController:alertController animated:YES completion:^{}];
}

//----------------   <WtfUi>   -----------------


-(instancetype) trigger :(NSString *)eventName :(JSO *) extraData
{
    if ([WtfEventAppResume isEqualToString:eventName]){
        //NSLog(@" !!!! TODO _myWebView trigger resume to page ...");
        [self evalJs:@"try{$(document).trigger('postresume');}catch(ex){}"];
    }else if([WtfEventAppPause isEqualToString:eventName]){
        //NSLog(@" !!!! TODO _myWebView trigger pause to page ...");
        [self evalJs:@"try{$(document).trigger('pause');}catch(ex){}"];
    }
    [super trigger:eventName :extraData];
    return self;
}

- (void) evalJs:(NSString *)js_s
{
    dispatch_async(dispatch_get_main_queue(), ^{
        //[WtfTools callWebViewDoJs:self.myWebView :js_s];
        //[self.class callWebViewDoJs:self.myWebView :js_s];
        
    });
}

+ (id) initHybridWebView :(Class)c :(WtfUi) caller
{
    WKWebViewConfiguration *
    webConfig = [[WKWebViewConfiguration alloc]init];
    
    // Setup WKUserContentController instance for injecting user script
    WKUserContentController* userController = [[WKUserContentController alloc]init];
    
    //script that's to be injected into the document
    NSString *js = [WtfTools readAssetInStr:@"WebViewJavascriptBridge.js" :YES];
    
    // Specify when and where and what user script needs to be injected into the web document
    WKUserScript* userScript
    = [[WKUserScript alloc] initWithSource:js
                             injectionTime:WKUserScriptInjectionTimeAtDocumentEnd
                          forMainFrameOnly:NO];
    
    [userController addUserScript:userScript];
    
    webConfig.userContentController= userController;
    
    //window.webkit.messageHandlers.nativejsb
    [webConfig.userContentController addScriptMessageHandler:[WKScriptMessageHandler initWithWtfUi:caller] name:@"nativejsb"];
    
    id rt = [[WKWebView alloc] initWithFrame:CGRectZero configuration:webConfig];
    
    //add scheme "local" ('coz WKWebView default don't support file)
    [WtfTools call_c_do_m:[WtfTools base64decode:@"V0tCcm93c2luZ0NvbnRleHRDb250cm9sbGVy"]
                         :[WtfTools base64decode:@"cmVnaXN0ZXJTY2hlbWVGb3JDdXN0b21Qcm90b2NvbDo="]
                         :@"local"];
    
    [NSURLProtocol registerClass:[WtfResourceURLProtocol class]];
    return rt;
}

-(void) initUi
{
    [super initUi];
    
    //    NSString *title = [[self.uiData getChild:@"title"] toString];
    //    if ([WtfTools isEmptyString:title]){
    //        title=@" - - - ";//TODO
    //    }
    //    [self on:WtfEventBeforeDisplay :^(NSString *eventName, JSO *extraData) {
    //
    //        NSLog(@"initUi() on eventName %@ ", eventName);
    //        [self resetTopBarStatus];
    //
    //        [self setTopBarTitle:title];
    //        [self setNeedsStatusBarAppearanceUpdate];
    //    } :nil];
    
    [self registerHandlerApi];
    
    //self.myWebView = [WtfTools initHybridWebView :[WKWebView class] :self];
    self.myWebView = [self.class initHybridWebView :[WKWebView class] :self];
    
    //self.myWebView.backgroundColor = [UIColor whiteColor];
    
    self.myWebView.navigationDelegate=self;//for start/stop/fail etc.
    self.myWebView.UIDelegate=self;//for alert/confirm/prompt
    
    // Edges prohibit sliding (default YES)
    self.myWebView.scrollView.bounces = NO;
    
    self.extendedLayoutIncludesOpaqueBars=YES;
    self.automaticallyAdjustsScrollViewInsets=NO;
    
    self.view = self.myWebView;
    
    [self spinnerInit];
    [self spinnerOn];
    
    NSString *address = [[self.uiData getChild:@"address"] toString];
    
    if ( [WtfTools isEmptyString:address] ){
        [WtfTools quickShowMsgMain:@"no address?" callback:^{
            [self finishUi];
        }];
        return;
    }
    
    //[WtfTools callWebViewLoadUrl:_myWebView :address];
    [WtfWKWebViewUi callWebViewLoadUrl:_myWebView :address];
}

- (void) resetTopBarBtn
{
    NSLog(@"resetTopBarBtn in WtfWKWebViewUi....");
    UIBarButtonItem *leftBar
    = [[UIBarButtonItem alloc]
       initWithImage:[UIImage imageNamed:@"btn_nav bar_left arrow"]//see Images.xcassets
       style:UIBarButtonItemStylePlain
       target:self
       action:@selector(finishUi)
       ];
    leftBar.tintColor = [UIColor blueColor];
    self.navigationItem.leftBarButtonItem=leftBar;
    
    //    self.navigationItem.leftBarButtonItem
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemReply
    //       target:self
    //       action:@selector(finishUi)];
    
    //    UIBarButtonItem *rightBtn
    //    = [[UIBarButtonItem alloc]
    //       initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:nil];
    //    self.navigationItem.rightBarButtonItem = rightBtn;
}

//register will cache the handler inside the memory for speeding up.  so it's important
- (void)registerHandlerApi{
    
    self.apiMap = [NSMutableDictionary dictionary];
    
    // get the appConfig:
    JSO *appConfig = [WtfTools wholeAppConfig];
    
    JSO *api_mapping = [appConfig getChild:@"api_mapping"];
    
    for (NSString *kkk in [api_mapping getChildKeys]) {
        NSString *apiname = [[api_mapping getChild:kkk] toString] ;
        WtfApi *api = [WtfTools getHybridApi:apiname];
        api.currentUi = self;
        //self.apiMap[kkk] = [api getHandler];//TODO see android registerHandler()
        self.apiMap[kkk] = api;
    }
}

//- (void) loadUrl:(NSString *)url{
//    NSURL *requesturl = [NSURL URLWithString:url];
//    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
//    [self.myWebView loadRequest:request];
//}

- (BOOL)prefersStatusBarHidden {
    NSLog(@"prefersStatusBarHidden returns NO");
    return NO;
}

-(UIStatusBarStyle)preferredStatusBarStyle{
    NSLog(@"preferredStatusBarStyle returns UIStatusBarStyleDefault");
    //return UIStatusBarStyleLightContent;
    return UIStatusBarStyleDefault;
}
@end
