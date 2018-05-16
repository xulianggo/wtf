#ifndef WtfUi_h
#define WtfUi_h

#import "Wtf.h"

#define WtfUi id<WtfUiProtocol>

//NOTES, in ObjC, protocol = interface, interface = class, yes, WTF !
@protocol WtfUiProtocol <NSObject>

@property (strong, nonatomic) JSO *uiData;

@property (strong, nonatomic) NSString *uiName;

@property (strong, nonatomic) JSO *responseData;

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;

@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

@required

@optional

-(void) initUi;//do init
-(void) closeUi;//do close

-(instancetype) on:(NSString *)eventName :(HybridEventHandler) handler;
//for some case, some initData is sent and use when trigger
-(instancetype) on:(NSString *)eventName :(HybridEventHandler) handler :(JSO *)initData;
-(instancetype) off:(NSString *)eventName;

-(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData;
-(instancetype) trigger :(NSString *)eventName;

//for top bar buttons:
-(void) resetTopBarBtn;

-(void) resetTopBar :(NSString *)mode;
-(void) resetTopBarStatus;
-(void) hideTopStatusBar;
-(void) showTopStatusBar;
-(void) hideTopBar;
-(void) showTopBar;
- (void)setTopBarTitle:(NSString *)title;

- (void) evalJs :(NSString *)js_s;

@end

#endif /* WtfUi_h */
