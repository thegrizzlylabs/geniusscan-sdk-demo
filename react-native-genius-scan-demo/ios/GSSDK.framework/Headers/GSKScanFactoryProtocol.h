//
//  GSKScanFactory.h
//  GSSDK
//
//  Created by Bruno Virlet on 6/2/16.
//
//

#import <Foundation/Foundation.h>

#ifndef GSK_GSKScanFactoryProtocol_h
#define GSK_GSKScanFactoryProtocol_h

#import "GSKScanProtocol.h"

/**
 Defines a factory protocol that generates a scan object. You pass this factory to the GSKCameraSession
 so that it generates objects of type GSKScanProtocol as an output.
 */
@protocol GSKScanFactoryProtocol <NSObject>

/**
 @return a new scan object conforming to the GSKScanProtocol protocol
 */
- (id<GSKScanProtocol>)createScan;

@end

#endif
