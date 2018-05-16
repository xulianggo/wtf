#ifndef Wtf_h
#define Wtf_h

#import "JSO.h"

#import <UIKit/UIKit.h>

//define WtfCallback:
typedef void (^WtfCallback)(JSO* responseData);

//define HybridHandler:  (NOTES, WtfApi.handler ?)
typedef void (^HybridHandler)(JSO * jso, WtfCallback responseCallback);

typedef void (^HybridDialogCallback)(UIAlertAction* action);

typedef void (^HybridEventHandler)(NSString *eventName, JSO* extraData);

typedef void (^WtfBlock)(void);

#define WtfHybridEventBeforeDisplay @"BeforeDisplay"
#define WtfHybridEventMemoryWarning @"MemoryWarning"
#define WtfHybridEventWhenClose @"WhenClose"
#define WtfHybridEventBeforeClose @"BeforeClose"
#define WtfHybridEventInitDone @"InitDone"
#define WtfHybridEventAppResume @"AppResume"
#define WtfHybridEventAppPause @"AppPause"

#define SINGLETON_shareInstance(classname) \
+ (classname *)shareInstance\
{\
static classname *_sharedInstance = nil;\
static dispatch_once_t onceToken;\
dispatch_once(&onceToken, ^{\
_sharedInstance = [[self alloc] init];\
});\
return _sharedInstance;\
}

#endif /* Wtf_h */
