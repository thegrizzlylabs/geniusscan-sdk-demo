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
using GoogleGson;

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

        //public Task<string> StartScanning(string jsonConfiguration)
        //{
        //    //Dictionary<string, Java.Lang.Object> configuration = new IJsonSerializer .Deserialize<Dictionary<string, Java.Lang.Object>>(jsonConfiguration);
        //    //Type empMapType = GoogleGson.Reflect.TypeToken.GetParameterized()<Map<String, Employee>>() { }.getType();
        //    //Dictionary<string, Java.Lang.Object> configuration = new Gson().FromJson(jsonConfiguration, )

        //    IDictionary<string, Java.Lang.Object> configuration = DeserializeJsonObject(new JsonParser().Parse(jsonConfiguration).AsJsonObject);
        //    return StartScanning(configuration);
        //}

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
                    // TODO : Properly serialize result in json
                    var pdfUrl = result.Result["pdfUrl"].ToString();
                    currentTask?.TrySetResult(pdfUrl);

                    //var json = "{\"pdfUrl\":\"" + result.Result["pdfUrl"].ToString() + "\"}";
                    //currentTask?.TrySetResult(json);
                }
                
            }
            catch (Exception e)
            {
                currentTask?.TrySetException(e);
            }
        }

        //object DeserializeJsonElement(JsonElement jsonElement)
        //{
        //    if (jsonElement.IsJsonPrimitive)
        //    {
        //        return DeserializeJsonPrimitive(jsonElement.AsJsonPrimitive);
        //    } else if (jsonElement.IsJsonArray)
        //    {
        //        return DeserializeJsonArray(jsonElement.AsJsonArray);
        //    } else if (jsonElement.IsJsonObject)
        //    {
        //        return DeserializeJsonObject(jsonElement.AsJsonObject);
        //    } else
        //    {
        //        return jsonElement;
        //    }
        //}

        //ICollection<Java.Lang.Object> DeserializeJsonArray(JsonArray jsonArray)
        //{
        //    var collection = new List<Java.Lang.Object>();
        //    var iterator = jsonArray.Iterator();
        //    while (iterator.HasNext)
        //    {
        //        var element = iterator.Next() as JsonElement;
        //        collection.Add(DeserializeJsonElement(element));
        //    }
        //    return collection;
        //}

        //IDictionary<string, Java.Lang.Object> DeserializeJsonObject(JsonObject jsonObject)
        //{
        //    var dictionary = new Java.Util.HashMap<string, Java.Lang.Object>();
        //    foreach (Java.Util.IMapEntry entry in jsonObject.EntrySet())
        //    {
        //        var value = entry.Value as JsonElement;
        //        Java.Lang.Object typedValue;
        //        if (value.IsJsonPrimitive)
        //        {
        //            typedValue = DeserializeJsonPrimitive(value.AsJsonPrimitive);
        //        } else
        //        {
        //            typedValue = value;
        //        }
        //        dictionary.Add(entry.Key.ToString(), typedValue);
        //    }
        //    return dictionary;
        //}

        //Java.Lang.Object DeserializeJsonPrimitive(JsonPrimitive jsonPrimitive)
        //{
        //    if (jsonPrimitive.IsString)
        //    {
        //        return jsonPrimitive.AsString;
        //    } else if (jsonPrimitive.IsNumber)
        //    {
        //        return jsonPrimitive.AsInt;
        //    } else if (jsonPrimitive.IsBoolean)
        //    {
        //        return jsonPrimitive.AsBoolean;
        //    } else
        //    {
        //        return jsonPrimitive;
        //    }
        //}
    }
}