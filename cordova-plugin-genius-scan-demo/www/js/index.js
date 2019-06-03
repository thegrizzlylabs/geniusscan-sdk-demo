function copy(filepath, toDirectory, filename, db) {
  window.resolveLocalFileSystemURL(filepath, function(fileEntry) {
    window.resolveLocalFileSystemURL(toDirectory, function(dirEntry) {
      dirEntry.getFile(filename, { create: true, exclusive: false }, function(
        targetFileEntry
      ) {
        fileEntry.file(function(file) {
          targetFileEntry.createWriter(function(fileWriter) {
            fileWriter.write(file);
          });
        });
      });
    });
  });
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
      cordova.plugins.GeniusScan.scanWithConfiguration(
        {source: 'camera'},
        function onSuccess(result) {
          // result contains the generated PDF as well as the individual scans.
          // you can use them any way you want.
          console.log(result);

          // Here we demonstrate how you can share the generated PDF through the system
          // share sheet, but that's also where you could send the document to your backend etc.
          var options = {
            files: [result.pdfUrl] // an array of filenames either locally or remotely
          };
          console.log(options);
          console.log(window.plugins.socialsharing);
          window.plugins.socialsharing.shareWithOptions(options,
                                                         function(result) { console.log("Successful share " + result); },
                                                         function(error) { console.log("Error sharing document: " + error); });
        },
        onError,
      );
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
