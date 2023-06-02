namespace SimpleDemo
{
	public partial class ScanFlowService
	{
        public partial Task Init(string licenseKey);

        public partial Task<string> StartScanning(string languagesDirectoryUrl);
    }
}

