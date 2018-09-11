//
//  GSQuadrangle.h
//  GSCamera
//
//  Created by Bruno Virlet on 4/1/15.
//  Copyright (c) 2015 Bruno Virlet. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>

#import <UIKit/UIKit.h>

extern const CGPoint GSKQuadrangleTopRightPoint;
extern const CGPoint GSKQuadrangleTopLeftPoint;
extern const CGPoint GSKQuadrangleBottomLeftPoint;
extern const CGPoint GSKQuadrangleBottomRightPoint;

/**
 Represents a quadrangular area of the photo, generally the document for which to correct
 the perspective.
 
 A quadrangle should always be expressed in the coordinates of the "up" image
 
 A normalized quadrangle: corners expressed in fraction of the image dimensions.
 */
@interface GSKQuadrangle : NSObject

+ (GSKQuadrangle *)emptyQuadrangle;
+ (GSKQuadrangle *)maxQuadrangle;
+ (GSKQuadrangle *)quadrangleFromCGRect:(CGRect)rect;
- (BOOL)isEmpty;
- (BOOL)isMax;
- (BOOL)isConvex;

@property (nonatomic, assign) CGPoint topLeft;
@property (nonatomic, assign) CGPoint topRight;
@property (nonatomic, assign) CGPoint bottomLeft;
@property (nonatomic, assign) CGPoint bottomRight;

/**
 Given a normalized Quadrangle, returns a new quadrangle expressed in the given
 size coordinates
 */
- (GSKQuadrangle *)scaleForSize:(CGSize)size;

/**
 Given a quadrangle expressed in the given size coordinates, returns a new quadrangle
 with normalized coordinates.
 */
- (GSKQuadrangle *)normalizedWithSize:(CGSize)size;

/**
 Given a quadrangle detected on a oriented image, this returns the quadrangle
 that would have been detected if once the image was rotated according to its orientation.
 
 IMPORTANT: this should only be applied to a normalized quadrangle
 */
- (GSKQuadrangle *)rotatedForUpOrientation:(UIImageOrientation)orientation;

/**
 Converts the quadrangle back to not take in account an image orientation
 */
- (GSKQuadrangle *)rotatedWithoutOrientation:(UIImageOrientation)orientation;

/**
 Ensures corners have their coordinates within [0, 1]
 */
- (GSKQuadrangle *)sanitized;
@end
