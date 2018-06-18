#import "WtfUi.h"

@interface WtfApi : NSObject

@property (nonatomic, weak) WtfUi currentUi;

- (WtfHandler) getHandler;

@end
