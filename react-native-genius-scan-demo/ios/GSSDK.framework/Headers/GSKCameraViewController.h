//
//  CaptureViewController.h
//  GeniusScan
//
//  Created by Bruno Virlet on 9/8/12.
//  Copyright (c) 2012 The Grizzly Labs. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "GSKCameraSession.h"

@class GSKCameraSession;
@class GSKView;
@class GSKShutterView;

/**
 A GSKCameraViewController is a ready-to-use camera interface for scanning documents.
 */
@interface GSKCameraViewController : UIViewController <GSKCameraSessionDelegate>

/**
 @param cameraSession The camera session to use for this view controller
 */
- (instancetype)initWithCameraSession:(GSKCameraSession *)cameraSession NS_DESIGNATED_INITIALIZER;

/**
 The camera view. Use this view in your layout.
 */
@property (nonatomic, readonly) UIView *cameraView;

/**
 The view containing the preview layer
 */
@property (nonatomic, strong) GSKView *captureView;

/**
 The view appearing when the photo is being taken, similar to a shutter screen on a real camera.
 */
@property (nonatomic, readonly) GSKShutterView *shutterView;

/**
 Trigger the photo
 */
- (void)takePhoto;

/**
 If YES, draws the detected document frame overlay in real time.
 Default is YES.
 */
@property (nonatomic, assign) BOOL drawsDocumentFrameOverlay;

/**
 Customize the overlay color.
 */
@property (nonatomic, copy) UIColor *overlayColor;

/**
 The camera session associated to this camera view controller
 */
@property (nonatomic, readonly) GSKCameraSession *cameraSession;

@end
