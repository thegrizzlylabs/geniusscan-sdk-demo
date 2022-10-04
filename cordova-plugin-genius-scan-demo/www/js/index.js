function onError(error) {
  alert("Error: " + JSON.stringify(error));
}

function copy(filepath, toDirectory, filename, callback) {
  window.resolveLocalFileSystemURL(filepath, function(fileEntry) {
    window.resolveLocalFileSystemURL(toDirectory, function(dirEntry) {
      dirEntry.getFile(filename, { create: true, exclusive: false }, function(targetFileEntry) {
        fileEntry.file(function(file) {
          targetFileEntry.createWriter(function(fileWriter) {
            fileWriter.write(file);
            callback();
          });
        });
      }, onError);
    }, onError);
  }, onError);
}

var app = {
  // Application Constructor
  initialize: function() {
    document.addEventListener(
      "deviceready",
      this.onDeviceReady.bind(this),
      false
    );
  },

  // deviceready Event Handler
  //
  // Bind any cordova events here. Common events are:
  // 'pause', 'resume', etc.
  onDeviceReady: function() {
    this.receivedEvent("deviceready");
    var scanButton = document.getElementById("scan_btn");

    function onError(error) {
      alert("Error: " + JSON.stringify(error));
    }

    function previewFile(fileUrl) {
      window.PreviewAnyFile.previewPath(
        function(result) { console.log("Successful share " + result); },
        function(error) { console.log("Error sharing document: " + error); },
        fileUrl
      );
    }

    scanButton.addEventListener("click", function() {
      var assetLanguageUri = `${cordova.file.applicationDirectory}www/eng.traineddata`
      var appFolder = window.cordova.platformId == 'android' ? cordova.file.externalDataDirectory : cordova.file.dataDirectory;
      copy(assetLanguageUri, appFolder, 'eng.traineddata', function() {
        var configuration = {
          source: 'camera',
          ocrConfiguration: {
            languages: ['eng'],
            languagesDirectoryUrl: appFolder
          }
        };
        cordova.plugins.GeniusScan.scanWithConfiguration(configuration, function onSuccess(result) {
          // The result object contains the captured scans as well as the multipage document
          console.log(JSON.stringify(result));

          // Here is how you can display the resulting document:
          previewFile(result.multiPageDocumentUrl);


          // You can also generate your document separately from selected pages:
          /*
          const documentUrl = appFolder + 'mydocument.pdf'
          const document = {
            pages: [{ imageUrl: result.scans[0].enhancedUrl }]
          };
          const documentGenerationConfiguration = { outputFileUrl: documentUrl };
          cordova.plugins.GeniusScan.generateDocument(document, documentGenerationConfiguration, function onSuccess(result) {
            previewFile(documentUrl);
          });
          */
        }, onError);
      });

    });
  },

  // Update DOM on a Received Event
  receivedEvent: function(id) {
    var parentElement = document.getElementById(id);
    var listeningElement = parentElement.querySelector(".listening");
    var receivedElement = parentElement.querySelector(".received");

    listeningElement.setAttribute("style", "display:none;");
    receivedElement.setAttribute("style", "display:block;");

    console.log("Received Event: " + id);
  }
};

app.initialize();
