function onError(error) {
  alert("Error: " + JSON.stringify(error));
}

var app = {
  initialize: function() {
    document.addEventListener("deviceready", this.onDeviceReady.bind(this), false);
  },

  onDeviceReady: function() {
    this.receivedEvent("deviceready");
    document.addEventListener("resume", this.onResume, false);

    // This code shows how to initialize the SDK with a license key.
    // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
    //
    // cordova.plugins.GeniusScan.setLicenseKey("<Your license key>", /* autoRefresh = */ true);

    document.getElementById("scan_btn").addEventListener("click", startScanFlow);
    document.getElementById("scan_readable_codes_btn").addEventListener("click", startReadableCodeScanFlow);
  },

  onResume: function(event) {
    if (event.pendingResult && event.pendingResult.pluginServiceName === "GeniusScan" && event.pendingResult.pluginStatus === "OK") {
      onScanFlowResult(event.pendingResult.result);
    }
  },

  receivedEvent: function(id) {
    var parentElement = document.getElementById(id);
    var listeningElement = parentElement.querySelector(".listening");
    var receivedElement = parentElement.querySelector(".received");

    listeningElement.setAttribute("style", "display:none;");
    receivedElement.setAttribute("style", "display:block;");

    console.log("Received Event: " + id);
  }
};

function startScanFlow() {
  var configuration = {
    source: 'camera',
    ocrConfiguration: {
      languages: ['en-US']
    }
  };
  cordova.plugins.GeniusScan.scanWithConfiguration(configuration, onScanFlowResult, onError);
}

function onScanFlowResult(result) {
  // The result object contains the captured scans as well as the multipage document
  console.log(JSON.stringify(result));

  // Here is how you can display the resulting document:
  previewFile(result.multiPageDocumentUrl);

  // You can also generate your document separately from selected pages:
  /*
  const documentUrl = appFolder + 'mydocument.pdf'
  const document = {
    pages: [{
      imageUrl: result.scans[0].enhancedUrl,
      hocrTextLayout: scanResult.scans[0].ocrResult.hocrTextLayout
    }]
  };
  const documentGenerationConfiguration = { outputFileUrl: documentUrl };
  cordova.plugins.GeniusScan.generateDocument(document, documentGenerationConfiguration, function onSuccess(result) {
    previewFile(documentUrl);
  });
  */
}

function startReadableCodeScanFlow() {
  var configuration = {
    isBatchModeEnabled: true,
    supportedCodeTypes: ['qr', 'code128', 'ean13']
  };
  cordova.plugins.GeniusScan.scanReadableCodesWithConfiguration(configuration, onReadableCodeScanFlowResult, onError);
}

function onReadableCodeScanFlowResult(result) {
  // The result object contains the detected readable codes
  console.log(JSON.stringify(result));

  // Display the detected codes
  var codesText = result.readableCodes.map(function(code) {
    return code.type + ': ' + code.value;
  }).join('\n');

  alert('Detected codes:\n' + (codesText || 'No codes detected'));
}

function previewFile(fileUrl) {
  window.PreviewAnyFile.previewPath(
    function(result) { console.log("Successful share " + result); },
    function(error) { console.log("Error sharing document: " + error); },
    fileUrl
  );
}

app.initialize();
