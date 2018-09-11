//
//  UIImage+GSCamera.h
//  GSCamera
//
//  Created by Bruno Virlet on 3/30/15.
//  Copyright (c) 2015 Bruno Virlet. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (GSKUtils)

- (UIImage *)gsk_scaleWithMaxSize:(CGFloat)maxSize quality:(CGInterpolationQuality)quality scale:(CGFloat)scale;
- (UIImage *)gsk_upOrientedImage;

@end
