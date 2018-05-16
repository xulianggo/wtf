#ifndef WtfHybridWKWebViewUi_h
#define WtfHybridWKWebViewUi_h

#import "WtfUi_UIViewController.h"

#import <WebKit/WebKit.h>

@interface WtfHybridWKWebViewUi :WtfUi_UIViewController <WKNavigationDelegate, WKUIDelegate>
{
}

@property (nonatomic, strong) WKWebView * myWebView;

//TODO add doJS()

@end

#endif /* WtfHybridWKWebViewUi_h */
