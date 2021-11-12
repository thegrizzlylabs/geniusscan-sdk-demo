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

    scanButton.addEventListener("click", function() {
      var assetLanguageUri = `${cordova.file.applicationDirectory}www/eng.traineddata`
      var fileLanguageFolder = window.cordova.platformId == 'android' ? cordova.file.externalDataDirectory : cordova.file.dataDirectory;
      copy(assetLanguageUri, fileLanguageFolder, 'eng.traineddata', function() {
        var configuration = {
          source: 'camera',
          ocrConfiguration: {
            languages: ['eng'],
            languagesDirectoryUrl: fileLanguageFolder
          }
        };
        cordova.plugins.GeniusScan.scanWithConfiguration(configuration, function onSuccess(result) {
          // result contains the generated PDF as well as the individual scans.
          // you can use them any way you want.
          console.log(JSON.stringify(result));

          // Here we demonstrate how you can display the generated PDF through the system
          // but that's also where you could send the document to your backend etc.
          window.PreviewAnyFile.previewPath(
            function(result) { console.log("Successful share " + result); },
            function(error) { console.log("Error sharing document: " + error); },
            result.pdfUrl
          );
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
