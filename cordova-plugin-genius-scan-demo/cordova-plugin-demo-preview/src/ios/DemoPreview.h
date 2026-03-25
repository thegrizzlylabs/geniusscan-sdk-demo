#import <Cordova/CDV.h>
#import <QuickLook/QuickLook.h>

@interface DemoPreview : CDVPlugin <QLPreviewControllerDataSource, QLPreviewControllerDelegate>

@property (nonatomic, strong) NSURL *previewURL;

- (void)previewPath:(CDVInvokedUrlCommand *)command;

@end
