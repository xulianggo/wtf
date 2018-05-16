#import "WtfUi.h"

@interface WtfUi_UIViewController : UIViewController <WtfUiProtocol>

{
    //private ...
    
    //protected
    @protected UIActivityIndicatorView * _myIndicatorView;
    
    //public ...
}

//NOTES: @property won't build var at runtime but a setter/getter. also regards a public

@property (strong, nonatomic) JSO *uiData;
@property (strong, nonatomic) NSString *uiName;

@property (strong, nonatomic) JSO *responseData;

@property (strong, nonatomic) NSMutableDictionary* uiApiHandlers;
@property (strong, nonatomic) NSMutableDictionary* uiEventHandlers;

- (void) spinnerInit;
- (void) spinnerOn;
- (void) spinnerOff;

@end
