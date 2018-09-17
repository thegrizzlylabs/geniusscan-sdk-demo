//
//  GSCEditFrameViewController.h
//  GSCamera
//
//  Created by Bruno Virlet on 4/4/15.
//  Copyright (c) 2015 Bruno Virlet. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GSKQuadrangle;

/**
 The GSKEditFrameViewController lets the user change a quadrangle.
 
 The quadrangle is a drawn as an overlay over an image. This typically lets the user edit the edges 
 of a document to crop it more accurately.
 */
@interface GSKEditFrameViewController : UIViewController

/**
 The image on which the quadrangle is overlaid.
 
 This is typically your original photo from the camera.
 */
@property (nonatomic, strong) UIImage *image;

/**
 Used to set the quadrangle to display in the view controller, and to retrieve the new quadrangle edited by the user.
 */
@property (nonatomic, assign) GSKQuadrangle *quadrangle;

/**
 The view on which the quadrangle is drawn.
 */
@property (nonatomic, readonly) UIView *frameView;

/**
 Customize the color of the shade within the quadrangle.
 */
@property (nonatomic, copy) UIColor *shadeColor;

/**
 Customize the color of the line used to draw the quadrangle.
 */
@property (nonatomic, copy) UIColor *lineColor;

@end
