namespace SimpleDemo;

public partial class MainPage : ContentPage
{

    private readonly ScanFlowService scanFlowService = new();

	public MainPage()
	{
		InitializeComponent();

        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
        //
        // scanFlowService.SetLicenseKey("<Your license key>");
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

            var documentUrl = await scanFlowService.StartScanning("file://" + appFolder);

            await Launcher.OpenAsync(new OpenFileRequest
            {
                File = new ReadOnlyFile(new Uri(documentUrl).AbsolutePath)
            });
        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
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


