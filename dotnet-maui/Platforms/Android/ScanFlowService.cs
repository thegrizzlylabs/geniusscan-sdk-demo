using GeniusScanSDK.Scanflow;
using GeniusScanSDK.ReadableCodeFlow;
namespace SimpleDemo
{
    public partial class ScanFlowService
    {

        public partial void SetLicenseKey(string licenseKey)
        {
            ScanFlow.SetLicenseKey(Platform.CurrentActivity, licenseKey, /* autoRefresh = */ true);
        }

        public partial Task<string> StartScanning()
        {
            var mainActivity = (MainActivity)Platform.CurrentActivity;
            return mainActivity.StartScanning();
        }

        public partial Task<string> StartScanningReadableCodes(Dictionary<string, object> configuration)
        {
            var mainActivity = (MainActivity)Platform.CurrentActivity;
            return mainActivity.StartScanningReadableCodes(configuration);
        }
    }
}

