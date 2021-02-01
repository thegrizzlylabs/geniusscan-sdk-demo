using System;
using Xamarin.Essentials;
using Xamarin.Forms;

namespace SimpleDemo.Forms
{
    public partial class MainPage : ContentPage
    {
        readonly IScanFlow scanFlow = DependencyService.Get<IScanFlow>();

        public MainPage()
        {
            InitializeComponent();
            try
            {
                scanFlow.Init("REPLACE WITH YOUR LICENSE");
            } catch (Exception e)
            {
                Console.WriteLine("Error initialising Genius Scan SDK: " + e.Message);
            }
        }

        async void StartScanning(object sender, EventArgs args)
        {
            //var configuration = new Dictionary<string, object>();
            //configuration["source"] = "camera";
            //configuration["multiPage"] = true;
            //configuration["defaultFilter"] = "blackAndWhite";
            //configuration["pdfMaxScanDimension"] = 2000;
            //configuration["postProcessingActions"] = new String[] { "rotate", "editFilter" };
            
            try
            {
                var pdfUrl = await scanFlow.StartScanning();

                //var jsonResult = await scanFlow.StartScanning(JsonSerializer.Serialize(configuration));
                //var result = JsonSerializer.Deserialize<Dictionary<string, object>>(jsonResult);
                //var pdfUrl = (string)result["pdfUrl"];

                await Share.RequestAsync(new ShareFileRequest
                {
                    File = new ShareFile(new Uri(pdfUrl).AbsolutePath),
                    Title = "Share PDF document"
                });
            } catch (Exception e)
            {
                await DisplayAlert("Alert", "Error: " + e.Message, "OK");
            }
        }
    }
}
