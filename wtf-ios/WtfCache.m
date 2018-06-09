#import "WtfCache.h"

@implementation WtfCache

{
    NSMutableArray * expireKeys;
}


//+ (WtfCache *) sharedInstance
//{
//    static dispatch_once_t predicate = 0;
//    __strong static id sharedObject = nil;
//    dispatch_once(&predicate, ^{
//        sharedObject = [[self alloc] init];
//    });
//
//    return sharedObject;
//}

- (id) init
{
    if ( self = [super init])
    {
        expireKeys = [[NSMutableArray alloc] init];
    }
    
    return self;
}

/**
 * Get Object
 *
 * @param NSString * key
 * @return id obj
 *
 **/

- (id) objectForKey: (NSString *) key
{
    id obj = [super objectForKey: key];
    
    if( obj == nil)
    {
        return nil;
    }
    
    BOOL expired = [self hasExpired: key];
    
    if( expired)
    {
        [super removeObjectForKey: key];
        return nil;
    }
    
    return obj;
}

/**
 * Set Object
 *
 * @param id obj
 * @param NSString * key
 * @param NSInteger seconds
 *
 */
- (void) setObject: (id) obj
            forKey: (NSString *) key
            expire: (NSInteger) seconds
{
    [super setObject: obj forKey: key];
    
    [self updateExpireKey: key expire: seconds];
}


/**
 * Update Expire Time for Key and Seconds to Expire
 *
 * @param NSString * key
 * @param NSInteger seconds
 *
 **/
- (void) updateExpireKey: (NSString *) key
                  expire: (NSInteger) seconds
{
    __block NSInteger index = -1;
    
    [expireKeys enumerateObjectsUsingBlock: ^(id obj, NSUInteger idx, BOOL *stop) {
        if([obj[@"key"] isEqualToString: key])
        {
            index = idx;
            *stop = YES;
            return;
        }
    }];
    
    NSNumber * expires = [NSNumber numberWithFloat: ([[NSDate date] timeIntervalSince1970] + seconds)];
    
    if( index > -1)
    {
        [[expireKeys objectAtIndex: index] setObject: expires forKey: key];
    }
    else
    {
        NSMutableDictionary * element = [[NSMutableDictionary alloc] init];
        [element setObject: key forKey: @"key"];
        [element setObject: expires forKey: @"expire"];
        
        [expireKeys addObject: element];
    }
    
}

/**
 * Has Expired for Key
 *
 **/
- (BOOL) hasExpired: (NSString *) key
{
    NSNumber * expiredObj = [self getExpireTime: key];
    if(nil==expiredObj)return NO;//patch wj
    NSDate * current = [NSDate date];
    
    NSDate * expireDate = [NSDate dateWithTimeIntervalSince1970: [expiredObj doubleValue]];
    
    return [current compare: expireDate] == NSOrderedDescending;
}

/**
 * Get Expire Time
 *
 * @param NSString * key
 * @param NSInteger
 *
 **/

- (NSNumber *) getExpireTime: (NSString *) key
{
    __block NSNumber * expire = nil;
    
    [expireKeys enumerateObjectsUsingBlock: ^(id obj, NSUInteger idx, BOOL *stop) {
        if([obj[@"key"] isEqualToString: key])
        {
            expire = obj[@"expire"];
            *stop = YES;
            return;
        }
    }];
    
    return expire;
}


@end
