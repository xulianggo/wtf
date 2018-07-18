#import "WtfCache.h"

@implementation WtfCache

{
    NSMutableDictionary * data;
    NSMutableDictionary * expireKeys;
}

- (id) init
{
    expireKeys = [[NSMutableDictionary alloc] init];
    data = [[NSMutableDictionary alloc] init];
    return self;
}

- (id) objectForKey: (NSString *) key
{
    id obj = [data objectForKey:key];
    
    if( obj == nil)
    {
        return nil;
    }
    
    BOOL expired = [self hasExpired: key];
    
    if( expired)
    {
        [data removeObjectForKey:key];
        return nil;
    }
    
    return obj;
}

- (void) setObject: (id) obj
            forKey: (NSString *) key
            expire: (NSInteger) seconds
{
    
    [data setObject:obj forKey:key];
    
    [self updateExpireKey: key expire: seconds];
}

- (void) setObject:(id) obj forKey:(NSString *) key
{
    [data setObject:obj forKey:key];
}

- (void) updateExpireKey: (NSString *) key
                  expire: (NSInteger) seconds
{
    //    __block NSInteger index = -1;
    //
    //    [expireKeys enumerateObjectsUsingBlock: ^(id obj, NSUInteger idx, BOOL *stop) {
    //        if([obj[@"key"] isEqualToString: key])
    //        {
    //            index = idx;
    //            *stop = YES;
    //            return;
    //        }
    //    }];
    //
    //    NSNumber * expires = [NSNumber numberWithFloat: ([[NSDate date] timeIntervalSince1970] + seconds)];
    //
    //    if( index > -1)
    //    {
    //        [[expireKeys objectAtIndex: index] setObject: expires forKey: key];
    //    }
    //    else
    //    {
    //        NSMutableDictionary * element = [[NSMutableDictionary alloc] init];
    //        [element setObject: key forKey: @"key"];
    //        [element setObject: expires forKey: @"expire"];
    //
    //        [expireKeys addObject: element];
    //    }
    [expireKeys setObject:[NSNumber numberWithFloat: ([[NSDate date] timeIntervalSince1970] + seconds)] forKey:key];
}

- (BOOL) hasExpired: (NSString *) key
{
    NSNumber * expiredObj = [self getExpireTime: key];
    if(nil==expiredObj)return NO;//patch wj
    NSDate * current = [NSDate date];
    
    NSDate * expireDate = [NSDate dateWithTimeIntervalSince1970: [expiredObj doubleValue]];
    
    return [current compare: expireDate] == NSOrderedDescending;
}


- (NSNumber *) getExpireTime: (NSString *) key
{
    //    __block NSNumber * expire = nil;
    //
    //    [expireKeys enumerateObjectsUsingBlock: ^(id obj, NSUInteger idx, BOOL *stop) {
    //        if([obj[@"key"] isEqualToString: key])
    //        {
    //            expire = obj[@"expire"];
    //            *stop = YES;
    //            return;
    //        }
    //    }];
    //     return expire;
    return [expireKeys objectForKey:key];
    
}

@end
