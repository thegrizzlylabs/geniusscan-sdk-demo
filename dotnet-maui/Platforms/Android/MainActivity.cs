using Android.App;
using Android.Content;
using Android.Content.PM;
using GeniusScanSDK.ScanFlow;

namespace SimpleDemo;

[Activity(Theme = "@style/Maui.SplashTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
    protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
    {
        if (!ScanFlowService.OnActivityResult(requestCode, resultCode, data))
        {
            base.OnActivityResult(requestCode, resultCode, data);
        }
    }
}

