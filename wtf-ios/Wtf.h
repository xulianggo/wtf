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

//TODO change to java-interface-alike stuff
typedef void (^WtfCallback)(JSO* responseData);

//TODO change to java-interface-alike stuff
typedef void (^HybridHandler)(JSO * jso, WtfCallback responseCallback);

typedef void (^HybridDialogCallback)(UIAlertAction* action);

//TODO change to java-interface-alike stuff
typedef void (^HybridEventHandler)(NSString *eventName, JSO* extraData);

typedef void (^WtfBlock)(void);

#define WtfEventBeforeDisplay @"BeforeDisplay"
#define WtfEventMemoryWarning @"MemoryWarning"
#define WtfEventWhenClose @"WhenClose"
#define WtfEventBeforeClose @"BeforeClose"
#define WtfEventInitDone @"InitDone"
#define WtfEventAppResume @"AppResume"
#define WtfEventAppPause @"AppPause"

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
