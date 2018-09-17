//
//  GSKPDFGenerator+Public.h
//  Pods
//
//  Created by Bruno Virlet on 10/25/16.
//
//

#import <GSSDK/GSSDK.h>

@interface GSKPDFGenerator (Public)


+ (nullable GSKPDFGenerator *)createWithDocument:(nonnull GSKPDFDocument *)document;

@end
