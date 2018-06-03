#ifndef WtfTools_h
#define WtfTools_h

// ignore NSLog() when NON-DEBUG mode to improve performance
#ifndef DEBUG
#undef NSLog
#define NSLog(args, ...)
#endif

#import "Wtf.h"
#import "JsEngineWebView.h"
#import "WtfUi.h"

#import "WtfHybridApi.h"

typedef void (^WtfUiCallback)(WtfUi ui);

@interface WtfTools : NSObject

@property (strong) JsEngineWebView *jswv;

//inner data store, hold until destroy.
@property (strong) JSO *_jAppConfig;

//TODO I18N
@property (strong) JSO *_i18n;

@property (strong) NSString *lang;//en,zh-cn,zh-*,kh,vn,th

//NOTES: use for events quick handling
@property (strong) WtfUi uiRoot;


//Singleton Pattern:
+ (WtfTools *) shareInstance;

+ (void) checkAppConfig;

+ (WtfHybridApi *) getHybridApi:(NSString *)name;

+ (void) startUi :(NSString *)strUiName
         initData:(JSO *) initData
        objCaller:(WtfUi )objCaller
         callback:(void (^)(WtfUi ui))callback;

+ (WtfUi ) startUi :(NSString *)strUiName
                  initData:(JSO *)initData
                 objCaller:(WtfUi )objCaller;

+ (JSO *) wholeAppConfig;
+ (JSO *) getAppConfig :(NSString *)key;
+ (UIViewController *) findTopRootView;

+ (NSString *) fullPathOfAsset :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename :(BOOL)removeComments;

+(BOOL) isEmptyString :(NSString *)s;

+ (void)appAlert:(NSString *)msg callback:(WtfBlock)callback;
+ (void)appAlert:(NSString *)msg;

+ (void) quickShowMsgMain :(NSString *)msg;

+ (void) quickShowMsgMain :(NSString *)msg callback:(WtfBlock)callback;

+ (void) quickConfirmMsgMain:(NSString *)msg handlerYes:(HybridDialogCallback) handlerYes handlerNo:(HybridDialogCallback) handlerNo;

+ (void) suspendApp;
+ (void) quitGracefully;

/**
 * Return a array of "matches".
 * Usage
 *   if ([quickRegExpMatch:@"reg" :@"txt" count] > 0) ...
 */
+(NSArray *) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt;
+(NSString *) quickRegExpReplace :(NSString *)regex_s :(NSString *)src :(NSString *)tgt;

+ (void) countDown:(double)interval initTime:(double)initTime block:(BOOL (^)(NSTimer *tm))block;

+ (NSInteger) os_compare:(Float32)tgt;
+ (BOOL) is_simulator;

+ (void) call_c_do_m_1 :(NSString *)ccc :(NSString *)mmm :(NSString *) vvv;
+ (NSString *) btoa:(NSString *)s;
+ (NSString *) base64encode:(NSString *)s;

+ (NSString *) atob:(NSString *)s;
+ (NSString *) base64decode:(NSString *)s;

+ (NSString *) I18N:(NSString *)key;
//+ (void) setI18N:(NSString *)_i18n;

+ (void)saveUserConfig :(NSString *)key :(NSString *)value_s :(BOOL)autosave;
+ (id)loadUserConfig :(NSString *)key;

+ (NSString *) getBuildType;

+ (void) notifyPause;
+ (void) notifyResume;

//////////////////// quick event handling {
@property (strong, nonatomic) NSMutableDictionary* eventHandlers;

-(instancetype) on:(NSString*)eventName :(HybridEventHandler)handler;
-(instancetype) on:(NSString*)eventName :(HybridEventHandler)handler :(JSO *)initData;
-(instancetype) off :(NSString *)eventName :(HybridEventHandler) handler;
-(instancetype) off:(NSString*)eventName;

-(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData;
-(instancetype) trigger :(NSString *)eventName;
//////////////////// quick event handling }

@end

#endif /* WtfTools_h */
