namespace SimpleDemo
{
	public partial class ScanFlowService
	{
        public partial void SetLicenseKey(string licenseKey);

        public partial Task<string> StartScanning();

        public partial Task<string> StartScanningReadableCodes(Dictionary<string, object> configuration);
    }
}

