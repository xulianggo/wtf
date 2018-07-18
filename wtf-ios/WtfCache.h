#import <Foundation/Foundation.h>

@interface WtfCache : NSObject

- (void) setObject:(id) obj forKey:(NSString *) key;
- (void) setObject: (id) obj forKey: (NSString *) key expire: (NSInteger) seconds;
- (id) objectForKey: (NSString *) key;

@end
