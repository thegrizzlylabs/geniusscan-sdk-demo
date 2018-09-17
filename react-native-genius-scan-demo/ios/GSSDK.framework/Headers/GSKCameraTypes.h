//
//  GSKCameraTypes.h
//  GSCamera
//
//  Created by Bruno Virlet on 4/8/15.
//  Copyright (c) 2015 Bruno Virlet. All rights reserved.
//

#ifndef GSCamera_GSKCameraTypes_h
#define GSCamera_GSKCameraTypes_h

#import "GSKScanProtocol.h"

typedef void(^GSKCameraStringBlock)(NSString *str);
typedef void(^GSKCameraVoidBlock)(void);
typedef void(^GSKCameraBoolBlock)(BOOL success);
typedef void(^GSKCameraErrorBlock)(NSError *error);
typedef void(^GSKCameraScanBlock)(id<GSKScanProtocol> scan);

#endif
