#ifndef WtfWKWebViewUi_h
#define WtfWKWebViewUi_h

#import "WtfUi_UIViewController.h"

#import <WebKit/WebKit.h>

@interface WtfWKWebViewUi :WtfUi_UIViewController <WKNavigationDelegate, WKUIDelegate>
{
}

@property (nonatomic, strong) WKWebView * myWebView;

//TODO add doJS()

@end

#endif /* WtfWKWebViewUi_h */
