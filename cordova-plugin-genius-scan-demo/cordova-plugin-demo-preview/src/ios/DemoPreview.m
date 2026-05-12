#import "DemoPreview.h"

@implementation DemoPreview

+ (NSURL *)urlFromPath:(NSString *)path
{
    if (path.length == 0) {
        return nil;
    }

    NSURL *url = [NSURL URLWithString:path];
    if (url == nil || url.scheme == nil) {
        return [NSURL fileURLWithPath:path];
    }
    return url;
}

- (void)previewPath:(CDVInvokedUrlCommand *)command
{
    NSString *path = [command.arguments objectAtIndex:0];
    NSURL *url = [[self class] urlFromPath:path];
    if (url == nil || !url.isFileURL) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid file URL."] callbackId:command.callbackId];
        return;
    }

    if (![[NSFileManager defaultManager] fileExistsAtPath:url.path]) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"File not found."] callbackId:command.callbackId];
        return;
    }

    self.previewURL = url;

    dispatch_async(dispatch_get_main_queue(), ^{
        QLPreviewController *previewController = [[QLPreviewController alloc] init];
        previewController.dataSource = self;
        previewController.delegate = self;
        [self.viewController presentViewController:previewController animated:YES completion:nil];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"opened"] callbackId:command.callbackId];
    });
}

- (NSInteger)numberOfPreviewItemsInPreviewController:(QLPreviewController *)controller
{
    return self.previewURL == nil ? 0 : 1;
}

- (id<QLPreviewItem>)previewController:(QLPreviewController *)controller previewItemAtIndex:(NSInteger)index
{
    return self.previewURL;
}

@end
