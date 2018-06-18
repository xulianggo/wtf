#import "WtfUi.h"

@interface WtfApi : NSObject

//@property (nonatomic, weak) WtfUi currentUi;
@property (nonatomic, strong) WtfUi currentUi;

- (WtfHandler) getHandler;

@end
