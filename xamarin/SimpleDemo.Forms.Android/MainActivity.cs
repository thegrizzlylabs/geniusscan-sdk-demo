using System;

using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.Runtime;
using Android.OS;
using Xamarin.Forms.Platform.Android;
using System.Threading.Tasks;
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

        public Task<string> StartScanning()
        {
            var configuration = new Dictionary<string, Java.Lang.Object>
            {
                { "source", "camera" }
            };
            return StartScanning(configuration);
        }

        private Task<string> StartScanning(IDictionary<string, Java.Lang.Object> configuration)
        {
            currentTask = new TaskCompletionSource<string>();
            PluginBridge.ScanWithConfiguration(Xamarin.Essentials.Platform.CurrentActivity, configuration);

            return currentTask.Task;
        }

        protected override void OnActivityResult(Int32 requestCode, Result resultCode, Intent data)
        {
            try
            {
                PromiseResult result = PluginBridge.GetPromiseResultFromActivityResult(this, requestCode, (int)resultCode, data);
                if (result.IsError)
                {
                    currentTask?.TrySetException(new Exception(result.ErrorMessage));
                } else
                {
                    var pdfUrl = result.Result["pdfUrl"].ToString();
                    currentTask?.TrySetResult(pdfUrl);
                }
                
            }
            catch (Exception e)
            {
                currentTask?.TrySetException(e);
            }
        }
    }
}