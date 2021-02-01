using System.Threading.Tasks;
using UIKit;
using Foundation;
using GeniusScanSDK.ScanFlow;

// Register ScanFlow into Xamarin.Forms's Dependency Service
[assembly: Xamarin.Forms.Dependency(typeof(SimpleDemo.Forms.iOS.ScanFlow))]

namespace SimpleDemo.Forms.iOS
{
    public class ScanFlow : IScanFlow
    {
        private GSKScanFlow scanFlow;

        public Task Init(string licenseKey)
        {
            var taskCompletionSource = new TaskCompletionSource<bool>();
            GSK.InitWithLicenseKey(licenseKey, (NSError error) => {
                taskCompletionSource.TrySetException(new NSErrorException(error));
            });
            taskCompletionSource.TrySetResult(true);
            return taskCompletionSource.Task;
        }

        public Task<string> StartScanning()
        {
            var configuration = new NSMutableDictionary();
            configuration.Add(new NSString("source"), new NSString("camera"));
            return StartScanning(configuration);
        }

        //public Task<string> StartScanning(string jsonConfiguration)
        //{
        //    var outError = new NSError();

        //    var nsData = new NSString(jsonConfiguration, NSStringEncoding.UTF8).Encode(NSStringEncoding.UTF8);
        //    var dictionary = (NSDictionary)NSJsonSerialization.Deserialize(nsData, 0, out outError);
        //    return startScanning(dictionary);
        //}

        private Task<string> StartScanning(NSDictionary configurationDictionary)
        {
            var taskCompletionSource = new TaskCompletionSource<string>();
            var outError = new NSError();

            var configuration = GSKScanFlowConfiguration_Dictionary.ConfigurationWithDictionary(new GSKScanFlowConfiguration(), configurationDictionary, out outError);
            scanFlow = GSKScanFlow.ScanFlowWithConfiguration(configuration);
            var viewController = UIApplication.SharedApplication.Delegate.GetWindow().RootViewController;
            scanFlow.StartFromViewController(viewController,
                (GSKScanFlowResult result) => {
                    //var resultData = NSJsonSerialization.Serialize(result.Dictionary(), 0, out outError);
                    //taskCompletionSource.TrySetResult(resultData.ToString());

                    // To get enhanced pages
                    // var scans = (NSArray)result.Dictionary().ValueForKey(new NSString("scans"));
                    // var scanUrl = scans.GetItem<NSDictionary>(0).ValueForKey(new NSString("enhancedUrl"));

                    var pdfUrl = result.Dictionary().ValueForKey(new NSString("pdfUrl"));
                    taskCompletionSource.TrySetResult(pdfUrl.ToString());

                },
                (NSError error) => { taskCompletionSource.TrySetException(new NSErrorException(error)); }
            );
            return taskCompletionSource.Task;
        }
    }
}
