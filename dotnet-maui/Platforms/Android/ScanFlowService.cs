using GeniusScanSDK.Scanflow;
namespace SimpleDemo
{
    public partial class ScanFlowService
    {

        public partial void SetLicenseKey(string licenseKey)
        {
            ScanFlow.SetLicenseKey(Platform.CurrentActivity, licenseKey, /* autoRefresh = */ true);
        }

        public partial Task<string> StartScanning(string languagesDirectoryUrl)
        {
            var mainActivity = (MainActivity)Platform.CurrentActivity;
            return mainActivity.StartScanning(languagesDirectoryUrl);
        }
    }
}

