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
            var documentUrl = await scanFlowService.StartScanning();

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

    async void StartReadableCodeScanning(object sender, EventArgs args)
    {
        try
        {
            var configuration = new Dictionary<string, object>
            {
                ["isBatchModeEnabled"] = true,
                ["supportedCodeTypes"] = new string[] { "qr", "code128", "ean13" }
            };

            var resultJson = await scanFlowService.StartScanningReadableCodes(configuration);

            await DisplayAlert("Readable Codes", $"Result: {resultJson}", "OK");
        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
            await DisplayAlert("Alert", "Error: " + e.Message, "OK");
        }
    }
}
