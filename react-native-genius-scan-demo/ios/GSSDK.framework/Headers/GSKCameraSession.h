//
//  GSCManager.h
//  GSCamera
//
//  Created by Bruno Virlet on 3/26/15.
//  Copyright (c) 2015 Bruno Virlet. All rights reserved.
//

#import <AVFoundation/AVFoundation.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "GSKCameraTypes.h"
#import "GSKScanProtocol.h"
#import "GSKScanFactoryProtocol.h"

extern NSString *const GSCManagerTakePhotoOptionManualKey;

@class GSKCaptureHandler;
@class GSKQuadrangle;
@class GSKCameraSession;

extern NSString *const kGSKCameraSessionErrorDomain;

typedef NS_ENUM(NSInteger, GSKCameraSessionFlashStatus) {
    GSKCameraSessionFlashStatusOn,
    GSKCameraSessionFlashStatusOff,
    GSKCameraSessionFlashStatusAuto,
};

typedef NS_ENUM(NSInteger, GSKCameraSessionError) {
    GSKCameraSessionErrorOther = -1,
    GSKCameraSessionErrorNotAuthorized = -2,
    GSKCameraSessionErrorLockDevice = -3,
    GSKCameraSessionErrorNoDevice = -4,
    GSKCameraSessionErrorInvalidSetupDependency = -5,
    GSKCameraSessionErrorNoVideoCaptureConnection = -6,
    GSKCameraSessionErrorInvalidData = -7,

    // Warnings
    GSKCameraSessionWarnLockDevice = -1003,
};

/**
 The delegate of GSKCameraSession must adopt the GSKCameraSessionDelegate protocol.

 This protocol gives information about the state of the camera session, from configuration to snapping of photos.
 */
@protocol GSKCameraSessionDelegate <NSObject>

/**
 The camera session setup finished with an error
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession setupFailedWithError:(NSError *)error;

/**
 The camera session encountered a non-fatal error
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didEncounterFailureWithError:(NSError *)error;

/**
 Camera session is going to take the photo
*/
- (void)cameraSessionWillSnapPhoto:(GSKCameraSession *)cameraSession;

/**
 Camera session just took picture but we haven't post-processed it yet
 */
- (void)cameraSessionDidSnapPhoto:(GSKCameraSession *)cameraSession;

/**
 The camera session couldn't snap a photo
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didFailToSnapPhotoWithError:(NSError *)error;

/**
 Camera session has finished processing the photo we just took

 @param scan The scan object that has been generated
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didGenerateScan:(id<GSKScanProtocol>)scan;

/**
 THe camera session failed with an error.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didFailWithError:(NSError *)error;

/**
 Identified quadrangle in last frame of photo stream

 @param quadrangle The quadrangle that has been found.
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession didFindQuadrangle:(GSKQuadrangle *)quadrangle;

@optional

/**
 The camera session setup finished successfully.

 This is your chance to hook up into
 the setup to change some params on the AVCaptureSession.

 This callback is called on a specific queue where all the session calls are serialized.
 */
- (void)cameraSessionDidSetup:(GSKCameraSession *)cameraSession;

/**
 Couldn't identify quadrangle in last frame of photo stream
 */
- (void)cameraSessionFailedToFindQuadrangle:(GSKCameraSession *)cameraSession;

/**
 Camera session is going to automatically take the photo
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession willAutoTriggerWithQuadrangle:(GSKQuadrangle *)quadrangle;

/**
 The camera session real-time quadrangle detection will soon validate the quadrangle
 */
- (void)cameraSessionIsAboutToChooseQuadrangle:(GSKCameraSession *)cameraSession;

/**
 The camera session is still looking for a quadrangle
*/
- (void)cameraSessionisSearchingQuadrangle:(GSKCameraSession *)cameraSession;

/**
 Camera session started running
 */
- (void)cameraSessionDidStart:(GSKCameraSession *)cameraSession;

/**
 Camera session stopped running
 */
- (void)cameraSessionDidStop:(GSKCameraSession *)cameraSession;

/**
 Camera session was interrupted - Can happen in various cases, including when the user goes to Split View mode
 */
- (void)cameraSession:(GSKCameraSession *)cameraSession wasInterruptedWithReason:(AVCaptureSessionInterruptionReason)reason;

/**
 Camera session resumes after interruption
 */
- (void)cameraSessionInterruptionEnded:(GSKCameraSession *)cameraSession;
@end

/**
 The GSKCameraSession class manages the interactions with the device camera.
 */
@interface GSKCameraSession : NSObject

/**
 @param scanFactory An object generating a scan.

 When a photo is generated, the camera session will generate an object implementing the GSKScanProtocol.
 By passing in this factory, this lets you use your own concrete implementation of this object.
 */
- (instancetype)initWithScanFactory:(id<GSKScanFactoryProtocol>)scanFactory NS_DESIGNATED_INITIALIZER;

@property (nonatomic, readonly) GSKCaptureHandler *cameraCaptureHandler;

@property (nonatomic, readonly) AVCaptureDevice *captureDevice;
@property (nonatomic, readonly) AVCaptureDeviceInput *captureInput;
@property (nonatomic, readonly) AVCaptureSession *captureSession;
@property (nonatomic, readonly) AVCaptureConnection *captureConnection;
@property (nonatomic, readonly) AVCaptureStillImageOutput *captureStillImageOutput;

/**
 Preloads the camera so that it's ready to stream preview and take photo
 */
- (void)setup;

/**
 Controls when the video session starts and stops delivering photos
 */
- (void)startSessionOnComplete:(GSKCameraVoidBlock)onComplete;
- (void)stopSessionOnComplete:(GSKCameraVoidBlock)onComplete;

/**
 Sets focus point.
 */
- (void)focusAtPoint:(CGPoint)focusPoint;

/**
 Change the flash status

 @param flashStatus The new status for the flash
 @param successBlock The block called on success
 @param errorBlock The block called on error
 */
- (void)setFlashStatus:(GSKCameraSessionFlashStatus)flashStatus onSuccess:(void (^)(GSKCameraSessionFlashStatus status))successBlock error:(void (^)(NSError *))errorBlock;

/**
 Manually take a photo
 */
- (void)takePhoto;

/**
 True for the duration of taking the photo and processing it. Observable.
 */
@property (nonatomic, assign, getter=isTakingPhoto) BOOL takingPhoto;

/**
Camera won't be used anymore in this session. Makes sure everything can be deallocated successfully.
 */
- (void)cleanup;

@property (nonatomic, assign, getter=isAutoTriggerEnabled) BOOL autoTriggerEnabled;

/**
 The camera session delegate.

 @see GSKCameraSessionDelegate
 */
@property (nonatomic, weak) id <GSKCameraSessionDelegate> delegate;

@end
