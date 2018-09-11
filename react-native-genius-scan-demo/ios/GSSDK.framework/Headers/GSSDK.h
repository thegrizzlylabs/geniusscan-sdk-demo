//
//  GSSDK.h
//  GSSDK
//
//  Created by Bruno Virlet on 6/20/15.
//
//

#import <UIKit/UIKit.h>

//! Project version number for GSSDK.
FOUNDATION_EXPORT double GSSDKVersionNumber;

//! Project version string for GSSDK.
FOUNDATION_EXPORT const unsigned char GSSDKVersionString[];

#import <GSSDK/GSKCameraTypes.h>

#import <GSSDK/GSKCore.h>
#import <GSSDK/UIImage+GSKUtils.h>
#import <GSSDK/GSKLog.h>

// UI
#import <GSSDK/GSKCameraSession.h>
#import <GSSDK/GSKCameraViewController.h>
#import <GSSDK/GSKEditFrameViewController.h>
#import <GSSDK/GSKShutterView.h>
#import <GSSDK/GSKView.h>
#import <GSSDK/GSKQuadrangle.h>
#import <GSSDK/GSKScanFactoryProtocol.h>
#import <GSSDK/GSKScanProtocol.h>

// PDF
#import <GSSDK/GSKPDFGenerator.h>
#import <GSSDK/GSKPDFGenerator+Public.h>
#import <GSSDK/GSKPDFPage.h>
#import <GSSDK/GSKPDFDocument.h>
#import <GSSDK/GSKPDFSize.h>
#import <GSSDK/GSKPDFLogger.h>
#import <GSSDK/GSKPDFImageProcessor.h>
#import <GSSDK/GSKPDFNoopImageProcessor.h>
#import <GSSDK/GSKPDFDefaultLogger.h>
