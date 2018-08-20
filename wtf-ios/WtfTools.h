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

#import "WtfApi.h"

#import "WtfCache.h"

typedef void (^WtfUiCallback)(WtfUi ui);

@interface WtfTools : NSObject

//internally mem store for object which be kept until instance destroyed:
@property (strong) NSMutableDictionary * _memStore;

//an internal JS Engine utilize System WebView:
@property (strong) JsEngineWebView *jswv;

//inner data store, hold until destroy.
@property (strong) JSO *_jAppConfig;

//TODO I18N
@property (strong) JSO *_i18n;

@property (strong) NSString *lang;//en,zh-cn,zh-*,kh,vn,th

//NOTES: use for events quick handling
@property (strong) WtfUi uiRoot;


//Singleton Pattern (for internal use):
+ (WtfTools *) sharedInstance;

//change language as en/zh-cn etc...
+(void) setLang :(NSString *)lang;


+ (WtfApi *) getHybridApi:(NSString *)name;

+ (void) finishRoot;
+ (void) startUi :(NSString *)strUiName
         initData:(JSO *) initData
        objCaller:(WtfUi )objCaller
         callback:(void (^)(WtfUi ui))callback;

+ (WtfUi ) startUi :(NSString *)strUiName
                  initData:(JSO *)initData
                 objCaller:(WtfUi )objCaller;

+ (JSO *) wholeAppConfig;
+ (JSO *) getAppConfig :(NSString *)key;
//+ (UIViewController *) findTopRootView;

//+ (NSString *) fullPathOfAsset :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename;
+(NSString *)readAssetInStr :(NSString *)filename :(BOOL)removeComments;

+(BOOL) isEmptyString :(NSString *)s;

+ (void)appAlert:(NSString *)msg :(WtfBlock)callback;
+ (void)appAlert:(NSString *)msg;

+ (void) quickShowMsgMain :(NSString *)msg;

+ (void) quickShowMsgMain :(NSString *)msg callback:(WtfBlock)callback;

+ (void) appConfirm:(NSString *)msg handlerYes:(WtfDialogCallback) handlerYes handlerNo:(WtfDialogCallback) handlerNo;

+ (void) suspendApp;
+ (void) KillAppSelf;

/**
 * Return a array of "matches".
 * Usage
 *   if ([quickRegExpMatch:@"reg" :@"txt"]) ...
 */
//+(NSArray *) quickRegExpMatchLine :(NSString *)regex_s :(NSString *)txt;//single line
+(BOOL) quickRegExpMatch :(NSString *)regex_s :(NSString *)txt;//multi line
+(NSString *) quickRegExpReplace :(NSString *)regex_s :(NSString *)src :(NSString *)tgt;

+ (void) countDown:(double)interval initTime:(double)initTime block:(BOOL (^)(NSTimer *tm))block;

+ (NSInteger) os_compare:(Float32)tgt;
+ (BOOL) is_simulator;

+ (void) call_c_do_m :(NSString *)ccc :(NSString *)mmm :(NSString *) vvv;

+ (NSString *) base64encode:(NSString *)s;

+ (NSString *) base64decode:(NSString *)s;

+ (NSString *) I18N:(NSString *)key;
//+ (void) setI18N:(NSString *)_i18n;

//+ (void)saveUserConfig :(NSString *)key :(NSString *)value_s :(BOOL)autosave;
//+ (id)loadUserConfig :(NSString *)key;

+ (void)saveUserConfig :(NSString *)key :(JSO *)jso;
+ (JSO *)loadUserConfig :(NSString *)key;

+ (NSString *) getBuildType;

+ (void) notifyPause;
+ (void) notifyResume;

//////////////////// mem store {
//@ref ._memStore
- (void) MemorySave :(NSString *)key :(id)val;
- (id) MemoryLoad :(NSString *)key;
//+ (void) MemorySave :(NSString *)key :(NSString *)val
//;+ (id) MemoryLoad :(NSString *)key
//;
//////////////////// mem store }

+ (NSString *) md5 :(NSString*) ipt;

//////////////////// quick event handling {
@property (strong, nonatomic) WtfCache* eventMap;
//@property (strong, nonatomic) NSMutableDictionary* eventHandlers;

+(instancetype) on:(NSString*)eventName :(WtfEventHandler) handler;
+(instancetype) on:(NSString*)eventName :(WtfEventHandler) handler :(JSO *)initData;
+(instancetype) on:(NSString *)eventName :(WtfEventHandler) handler :(JSO *)initData :(NSInteger)expire;//new 201806 for TTL
+(instancetype) off :(NSString *)eventName :(WtfEventHandler) handler;
+(instancetype) off:(NSString*)eventName;
+(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData;
+(instancetype) trigger :(NSString *)eventName;

//-(instancetype) on:(NSString*)eventName :(WtfEventHandler) handler;
//-(instancetype) on:(NSString*)eventName :(WtfEventHandler) handler :(JSO *)initData;
//-(instancetype) on:(NSString *)eventName :(WtfEventHandler) handler :(JSO *)initData :(NSInteger)expire;//new 201806 for TTL
//-(instancetype) off :(NSString *)eventName :(WtfEventHandler) handler;
//-(instancetype) off:(NSString*)eventName;
//-(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData;
//-(instancetype) trigger :(NSString *)eventName;
//////////////////// quick event handling }

@end

#endif /* WtfTools_h */
