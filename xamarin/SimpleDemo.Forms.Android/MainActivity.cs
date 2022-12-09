using System;

using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.Runtime;
using Android.OS;
using Xamarin.Forms.Platform.Android;
using System.Threading.Tasks;
using Com.Geniusscansdk;
using Com.Geniusscansdk.Pdf;
using GeniusScanSDK.Core;
using GeniusScanSDK.Scanflow;
using System.Collections.Generic;

// Register MainActivity into Xamarin.Forms's Dependency Service
[assembly: Xamarin.Forms.Dependency(typeof(SimpleDemo.Forms.Droid.MainActivity))]

namespace SimpleDemo.Forms.Droid
{
    [Activity(Icon = "@mipmap/ic_launcher", Theme = "@style/MainTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize)]
    public class MainActivity : FormsAppCompatActivity, IScanFlow
    {
        private static TaskCompletionSource<string> currentTask = null;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            TabLayoutResource = Resource.Layout.Tabbar;
            ToolbarResource = Resource.Layout.Toolbar;

            base.OnCreate(savedInstanceState);

            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            global::Xamarin.Forms.Forms.Init(this, savedInstanceState);
            LoadApplication(new App());
        }

        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);

            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        public Task Init(string licenseKey)
        {
            try
            {
                ScanFlow.Init(Xamarin.Essentials.Platform.CurrentActivity, licenseKey);
                return Task.FromResult(true);
            } catch (Exception e)
            {
                return Task.FromException(e);
            }
        }

        public Task<string> StartScanning(string languagesDirectoryUrl)
        {
            var ocrConfiguration = new ScanConfiguration.OcrConfiguration();
            ocrConfiguration.Languages = new List<String> { "eng" };
            ocrConfiguration.LanguagesDirectory = new Java.IO.File(Android.Net.Uri.Parse(languagesDirectoryUrl).Path);

            var configuration = new ScanConfiguration();
            configuration.ScanSource = ScanConfiguration.Source.Camera;
            configuration.ScanOcrConfiguration = ocrConfiguration;

            currentTask = new TaskCompletionSource<string>();
            ScanFlow.ScanWithConfiguration(Xamarin.Essentials.Platform.CurrentActivity, configuration);
            return currentTask.Task;
        }

        protected override void OnActivityResult(Int32 requestCode, Result resultCode, Intent data)
        {
            if (requestCode == ScanFlow.ScanRequest && resultCode == Result.Ok && data != null)
            {
                try
                {
                    // Here is how you can access the resulting document:

                    ScanResult result = ScanFlow.GetScanResultFromActivityResult(data);
                    currentTask?.TrySetResult(result.MultiPageDocument.AbsolutePath);

                    // You can also generate your document separately from selected pages:
                    //var imagePath = (result.Scans[0] as ScanResult.Scan).EnhancedImageFile.Path;
                    //var pages = new List<PDFPage> { new PDFPage(imagePath, ScanConfiguration.PdfPageSize.Fit.ToPDFSize(), null) };
                    //var document = new PDFDocument("title", null, null, new Java.Util.Date(), new Java.Util.Date(), pages);
                    //var outputFilePath = Path.Combine(GetExternalFilesDir(null).Path, "output.pdf");
                    //var configuration = new DocumentGenerator.Configuration();
                    //configuration.OutputFile = new Java.IO.File(outputFilePath);
                    //new DocumentGenerator().GenerateDocument(document, configuration);
                    //currentTask?.TrySetResult(outputFilePath);

                }
                catch (Exception e)
                {
                    currentTask?.TrySetException(new Exception(e.Message));
                }
            }
            else
            {
                currentTask?.TrySetException(new Exception("Scan was cancelled"));
            }
        }
    }
}