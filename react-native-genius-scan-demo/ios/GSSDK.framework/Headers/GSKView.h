//
//  GSCameraView.h
//  GSCamera
//
//  Created by Bruno Virlet on 3/30/15.
//  Copyright (c) 2015 Bruno Virlet. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <AVFoundation/AVFoundation.h>

@class GSKView;
@class GSKQuadrangle;

@protocol GSCViewDelegate <NSObject>

- (void)cameraView:(GSKView *)cameraView requestedFocusAtPoint:(CGPoint)focusPoint;

@end

@interface GSKView : UIView

// Handle interface orientation change
- (void)initializeRotationWithInterfaceOrientation:(UIInterfaceOrientation)orientation;
- (void)rotateWithCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator;

// Displays the focus indicator at the requested location
- (void)animateFocusAtLocation:(CGPoint)location;

/// Updates the quadrangle overlay. Removes the quadrangle if @param quadrangle is nil.
- (void)updateQuadrangle:(GSKQuadrangle *)quadrangle;

- (void)setCaptureSession:(AVCaptureSession *)session;

- (void)pausePreview;
- (void)resumePreview;

/**
 The document frame layer
 */
@property (nonatomic, readonly) CAShapeLayer *frameLayer;
@property (nonatomic, readonly) CAShapeLayer *snapFrameLayer;

@property (nonatomic, copy) UIColor *overlayColor;

@property (nonatomic, weak) id <GSCViewDelegate> delegate;

@end

