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
            try
            {
                var documentUrl = await scanFlow.StartScanning();

                await Share.RequestAsync(new ShareFileRequest
                {
                    File = new ShareFile(new Uri(documentUrl).AbsolutePath),
                    Title = "Share PDF document"
                });
            } catch (Exception e)
            {
                await DisplayAlert("Alert", "Error: " + e.Message, "OK");
            }
        }
    }
}
