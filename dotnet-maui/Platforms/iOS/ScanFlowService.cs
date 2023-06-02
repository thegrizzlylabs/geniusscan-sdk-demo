using Foundation;
using GeniusScanSDK.ScanFlow;
using UIKit;

namespace SimpleDemo
{
	public partial class ScanFlowService
	{
        public partial Task Init(string licenseKey)
        {
            var taskCompletionSource = new TaskCompletionSource<bool>();
            GSK.InitWithLicenseKey(licenseKey, (NSError error) => {
                taskCompletionSource.TrySetException(new NSErrorException(error));
            });
            taskCompletionSource.TrySetResult(true);
            return taskCompletionSource.Task;
        }

        public partial Task<string> StartScanning(string languagesDirectoryUrl)
        {
            var ocrConfiguration = new NSMutableDictionary
            {
                { new NSString("languages"), NSArray.FromStrings(new string[] { "eng" }) },
                { new NSString("languagesDirectoryUrl"), new NSString(languagesDirectoryUrl) }
            };

            var configuration = new NSMutableDictionary
            {
                { new NSString("source"), new NSString("camera") },
                { new NSString("ocrConfiguration"), ocrConfiguration }
            };

            return StartScanning(configuration);
        }

        private Task<string> StartScanning(NSDictionary configurationDictionary)
        {
            var taskCompletionSource = new TaskCompletionSource<string>();
            var outError = new NSError();

            var configuration = GSKScanFlowConfiguration_Dictionary.ConfigurationWithDictionary(new GSKScanFlowConfiguration(), configurationDictionary, out outError);
            var scanFlow = GSKScanFlow.ScanFlowWithConfiguration(configuration);
            var viewController = UIApplication.SharedApplication.Delegate.GetWindow().RootViewController;
            scanFlow.StartFromViewController(viewController,
                (GSKScanFlowResult result) => {
                    // Here is how you can access the resulting document:

                    var multiPageDocumentUrl = result.Dictionary().ValueForKey(new NSString("multiPageDocumentUrl"));
                    taskCompletionSource.TrySetResult(multiPageDocumentUrl.ToString());

                    // You can also generate your document separately from selected pages:

                    /*
                    var documents = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
                    var outputFileUrl = new NSString("file://" + Path.Combine(documents, "output.pdf"));
                    var generatorConfigurationDictionary = new NSMutableDictionary();
                    generatorConfigurationDictionary.Add(new NSString("outputFileUrl"), outputFileUrl);

                    var scans = (NSArray)result.Dictionary().ValueForKey(new NSString("scans"));
                    var scanUrl = scans.GetItem<NSDictionary>(0).ValueForKey(new NSString("enhancedUrl"));
                    var hocr = scans.GetItem<NSDictionary>(0).ValueForKey(new NSString("ocrResult")).ValueForKey(new NSString("hocrTextLayout"));
                    var pageDictionary = new NSMutableDictionary();
                    pageDictionary.Add(new NSString("imageUrl"), scanUrl);
                    pageDictionary.Add(new NSString("hocrTextLayout"), hocr);

                    var pages = new NSMutableArray();
                    pages.Add(pageDictionary);

                    var documentDictionary = new NSMutableDictionary();
                    documentDictionary.Add(new NSString("pages"), pages);

                    var document = new GSKPDFDocument(documentDictionary, out outError);
                    var generatorConfiguration = new GSKDocumentGeneratorConfiguration(generatorConfigurationDictionary, out outError);
                    new GSKDocumentGenerator().Generate(document, generatorConfiguration, (NSError error) => {
                           taskCompletionSource.TrySetException(new NSErrorException(error));
                    });

                    taskCompletionSource.TrySetResult(outputFileUrl);
                    */

                },
                (NSError error) => { taskCompletionSource.TrySetException(new NSErrorException(error)); }
            );
            return taskCompletionSource.Task;
        }
    }
}

