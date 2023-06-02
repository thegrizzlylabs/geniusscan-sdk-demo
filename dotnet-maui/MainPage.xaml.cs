namespace SimpleDemo;

public partial class MainPage : ContentPage
{

    private readonly ScanFlowService scanFlowService = new();

	public MainPage()
	{
		InitializeComponent();

        try
        {
            scanFlowService.Init("REPLACE WITH YOUR LICENSE");
        }
        catch (Exception e)
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


