using Android.App;
using Android.Content;
using Android.Content.PM;
using GeniusScanSDK.Scanflow;

namespace SimpleDemo;

[Activity(Theme = "@style/Maui.SplashTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
    private static TaskCompletionSource<string> currentTask = null;

    public Task<string> StartScanning()
    {
        var ocrConfiguration = new ScanConfiguration.OcrConfiguration
        {
            Languages = new List<String> { "en-US" },
        };

        var configuration = new ScanConfiguration
        {
            ScanSource = ScanConfiguration.Source.Camera,
            ScanOcrConfiguration = ocrConfiguration
        };

        currentTask = new TaskCompletionSource<string>();
        ScanFlow.ScanWithConfiguration(this, configuration);
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
                /*
                var imagePath = (result.Scans[0] as ScanResult.Scan).EnhancedImageFile.Path;
                var hocr = (result.Scans[0] as ScanResult.Scan).OcrResult.HocrTextLayout;
                var pages = new List<PDFPage> { new PDFPage(imagePath, ScanConfiguration.PdfPageSize.Fit.ToPDFSize(), new TextLayout(hocr)) };
                var document = new PDFDocument("title", null, null, new Java.Util.Date(), new Java.Util.Date(), pages);
                var outputFilePath = Path.Combine(GetExternalFilesDir(null).Path, "output.pdf");
                var configuration = new DocumentGenerator.Configuration();
                configuration.OutputFile = new Java.IO.File(outputFilePath);
                new DocumentGenerator().GenerateDocument(Xamarin.Essentials.Platform.CurrentActivity, document, configuration);
                currentTask?.TrySetResult(outputFilePath);
                */

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

