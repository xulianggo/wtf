#import <WebKit/WebKit.h>

//trick: utilize WebView as js engine.... have no time, so only WKWebView supported for now (iOS 8+)

@interface JsEngineWebView : WKWebView <WKNavigationDelegate, WKUIDelegate>
{
}

@end
