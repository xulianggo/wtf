#ifndef Wtf_h
#define Wtf_h

#import "JSO.h"

#import <UIKit/UIKit.h>

//TODO remove function point asap

//@ref WtfCallback in Android
//@protocol WtfCallback123
////@property
//@required
//@optional
//-(void) onCallback:(JSO*) responseData;
//@end

typedef void (^WtfCallback)(JSO* responseData);

//@ref (WtfHandler) ApiWtf.getHandler;
typedef void (^WtfHandler)(JSO * jso, WtfCallback responseCallback);

//sync android WtfDialogCallback
typedef void (^WtfDialogCallback)(UIAlertAction* action);

//@ref sounds like "WtfEventCallback", but for now, we can merge with WtfCallback yet, so maybe in far future?
typedef void (^WtfEventHandler)(NSString *eventName, JSO* extraData);

typedef void (^WtfBlock)(void);

#define WtfEventBeforeDisplay @"WtfEventBeforeDisplay"
#define WtfEventMemoryWarning @"WtfEventMemoryWarning"
#define WtfEventWhenClose @"WtfEventWhenClose"
#define WtfEventBeforeClose @"WtfEventBeforeClose"
#define WtfEventAfterClose @"WtfEventAfterClose"
#define WtfEventInitDone @"WtfEventInitDone"
#define WtfEventAppResume @"WtfEventAppResume"
#define WtfEventAppPause @"WtfEventAppPause"

#define SINGLETON_shareInstance(classname) \
+ (classname *)shareInstance\
{\
static classname *_sharedInstance = nil;\
static dispatch_once_t onceToken;\
dispatch_once(&onceToken, ^{\
if(nil==_sharedInstance) _sharedInstance = [[self alloc] init];\
});\
return _sharedInstance;\
}

#endif /* Wtf_h */
