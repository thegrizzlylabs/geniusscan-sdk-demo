using Android.App;
using Android.Content;
using Android.Content.PM;
using GeniusScanSDK.Scanflow;
using GeniusScanSDK.ReadableCodeFlow;

namespace SimpleDemo;

[Activity(Theme = "@style/Maui.SplashTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
    private static TaskCompletionSource<string> currentTask = null;
    private static TaskCompletionSource<string> currentReadableCodeTask = null;

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

    public Task<string> StartScanningReadableCodes(Dictionary<string, object> configuration)
    {
        var configurationMap = CreateReadableCodeConfigurationMap(configuration);

        currentReadableCodeTask = new TaskCompletionSource<string>();
        PluginBridge.ScanReadableCodesWithConfiguration(this, configurationMap);
        return currentReadableCodeTask.Task;
    }

    private Dictionary<string, Java.Lang.Object> CreateReadableCodeConfigurationMap(Dictionary<string, object> configuration)
    {
        var configurationMap = new Dictionary<string, Java.Lang.Object>();

        foreach (var kvp in configuration)
        {
            Java.Lang.Object value = null;

            if (kvp.Value is bool boolValue)
            {
                value = Java.Lang.Boolean.ValueOf(boolValue);
            }
            else if (kvp.Value is string[] stringArray)
            {
                var javaList = new Java.Util.ArrayList();
                foreach (var str in stringArray)
                {
                    javaList.Add(str);
                }
                value = javaList;
            }
            else if (kvp.Value is string stringValue)
            {
                value = new Java.Lang.String(stringValue);
            }

            if (value != null)
            {
                configurationMap.Add(kvp.Key, value);
            }
        }

        return configurationMap;
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
        else if (requestCode == ReadableCodeFlow.RequestCode && resultCode == Result.Ok && data != null)
        {
            var readableCodeResult = ReadableCodeFlow.GetResultFromActivityResult(data);

            if (readableCodeResult is IReadableCodeFlowResult.Success)
            {
                // Create result dictionary matching the iOS format
                var resultDict = new Dictionary<string, object>
                {
                    ["readableCodes"] = ((IReadableCodeFlowResult.Success)readableCodeResult).Codes.Select(code => new Dictionary<string, string>
                    {
                        ["value"] = code.Value,
                        ["type"] = code.GetType().ToString().ToLowerInvariant()
                    }).ToList()
                };

                var jsonResult = System.Text.Json.JsonSerializer.Serialize(resultDict, new System.Text.Json.JsonSerializerOptions { WriteIndented = true });
                currentReadableCodeTask?.TrySetResult(jsonResult);
            }
            else if (readableCodeResult is IReadableCodeFlowResult.Canceled)
            {
                currentReadableCodeTask?.TrySetException(new Exception("ReadableCode scan was cancelled"));
            }
            else
            {
                var erroMessage = ((IReadableCodeFlowResult.Error)readableCodeResult).Message;
                currentReadableCodeTask?.TrySetException(new Exception(erroMessage));
            }
        }
        else
        {
            if (requestCode == ScanFlow.ScanRequest)
            {
                currentTask?.TrySetException(new Exception("Scan was cancelled"));
            }
            else if (requestCode == ReadableCodeFlow.RequestCode)
            {
                currentReadableCodeTask?.TrySetException(new Exception("ReadableCode scan was cancelled"));
            }
        }
    }
}

