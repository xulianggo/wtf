#import "WtfUi.h"

@interface WtfHybridApi : NSObject

//TODO from android setCallerUi()/getCallerUi()


@property (nonatomic, weak) WtfUi currentUi;

//TODO see if merge with android handler()
- (HybridHandler) getHandler;

@end
