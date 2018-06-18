#import "JsEngineWebView.h"

#import "WtfTools.h"

@implementation JsEngineWebView

//- (void) webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation
//{
//}
//

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
    
    id rt = [[WKWebView alloc] initWithFrame:CGRectZero configuration:webConfig];
    
    //add scheme "local" ('coz WKWebView default don't support file)
    [WtfTools call_c_do_m:[WtfTools base64decode:@"V0tCcm93c2luZ0NvbnRleHRDb250cm9sbGVy"]
                         :[WtfTools base64decode:@"cmVnaXN0ZXJTY2hlbWVGb3JDdXN0b21Qcm90b2NvbDo="]
                         :@"local"];
    
    //[NSURLProtocol registerClass:[WtfResourceURLProtocol class]];
    return rt;
}

//register will cache the handler inside the memory for speeding up.  so it's important
- (void)registerHandlerApi{
    
    // get the appConfig:
    JSO *appConfig = [WtfTools wholeAppConfig];
    
    JSO *api_mapping = [appConfig getChild:@"api_mapping"];
    
    for (NSString *kkk in [api_mapping getChildKeys]) {
        NSString *apiname = [[api_mapping getChild:kkk] toString] ;
        WtfApi *api = [WtfTools getHybridApi:apiname];
    }
}

//- (void) loadUrl:(NSString *)url{
//    NSURL *requesturl = [NSURL URLWithString:url];
//    NSURLRequest *request = [NSURLRequest requestWithURL:requesturl];
//    [self.myWebView loadRequest:request];
//}

@end
