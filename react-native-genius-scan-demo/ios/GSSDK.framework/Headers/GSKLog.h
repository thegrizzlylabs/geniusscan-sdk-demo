//
//  GSKLogger.h
//  GSSDK
//
//  Created by Patrick Nollet on 20/10/2016.
//
//

#import <Foundation/Foundation.h>

@protocol GSKLogger <NSObject>

- (void)logVerbose:(NSString *)message;
- (void)logDebug:(NSString *)message;
- (void)logInfo:(NSString *)message;
- (void)logWarn:(NSString *)message;
- (void)logError:(NSString *)message;

@end

@interface GSKLog : NSObject
/**
 Set your own logger if you want to get more advanced logs than the ones that NSLog provide
 */
+ (void)setLogger:(id<GSKLogger>)logger;
@end
