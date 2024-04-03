namespace SimpleDemo
{
	public partial class ScanFlowService
	{
        public partial void SetLicenseKey(string licenseKey);

        public partial Task<string> StartScanning();
    }
}

