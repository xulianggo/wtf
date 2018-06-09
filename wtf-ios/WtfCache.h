//@ref https://stackoverflow.com/questions/10132273/cache-expiration-implementation-using-nscache
#import <Foundation/Foundation.h>

@interface WtfCache : NSCache
//+ (PTCache *) sharedInstance;
- (void) setObject: (id) obj
            forKey: (NSString *) key
            expire: (NSInteger) seconds;

- (id) objectForKey: (NSString *) key;
@end
