using Com.Geniusscansdk.Scanflow;
namespace SimpleDemo
{
    public partial class ScanFlowService
    {
        public partial Task Init(string licenseKey)
        {
            try
            {
                ScanFlow.Init(Platform.CurrentActivity, licenseKey);
                return Task.FromResult(true);
            }
            catch (Exception e)
            {
                return Task.FromException(e);
            }
        }

        public partial Task<string> StartScanning(string languagesDirectoryUrl)
        {
            var mainActivity = (MainActivity)Platform.CurrentActivity;
            return mainActivity.StartScanning(languagesDirectoryUrl);
        }
    }
}

