using System;
using System.IO;
using System.Net.Http;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;
using Xamarin.Forms.PlatformConfiguration;

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
                var appFolder = Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
                var languageFilePath = Path.Combine(appFolder, "eng.traineddata");
                Console.WriteLine(languageFilePath);
                if (!File.Exists(languageFilePath))
                {
                    await DownloadFileAsync("https://github.com/tesseract-ocr/tessdata_fast/raw/main/eng.traineddata", languageFilePath);
                }

                var documentUrl = await scanFlow.StartScanning("file://" + appFolder);

                await Launcher.OpenAsync(new OpenFileRequest
                {
                    File = new ReadOnlyFile(new Uri(documentUrl).AbsolutePath)
                });
            } catch (Exception e)
            {
                await DisplayAlert("Alert", "Error: " + e.Message, "OK");
            }
        }

        private async Task DownloadFileAsync(string fileUrl, string downloadedFilePath)
        {
            var client = new HttpClient();

            var downloadStream = await client.GetStreamAsync(fileUrl);

            var fileStream = File.Create(downloadedFilePath);

            await downloadStream.CopyToAsync(fileStream);
        }
    }
}
