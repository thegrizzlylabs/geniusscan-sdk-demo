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
    var imagePreview = document.getElementById("img_preview")
    var cameraButton = document.getElementById("scan_camera_btn");
    var imageButton = document.getElementById("scan_img_btn");
    var pdfButton = document.getElementById("scan_pdf_btn");
    var bwFilterCheckbox = document.getElementById("bw_filter");

    var assetImgUri = `${cordova.file.applicationDirectory}www/img/scan.jpg`;
    var cacheImgUri = `${cordova.file.cacheDirectory}scan.jpg`;
    // On Android, we can't can't manipulate an assset file file:///android_asset/xxxx
    // as a normal file uri (https://stackoverflow.com/questions/4820816/how-to-get-uri-from-an-asset-file)
    // so we first need to copy that file in cache...
    copy(assetImgUri, cordova.file.cacheDirectory, "scan.jpg");

    var enhancedImage;

    function onError(error) {
      alert("Error: " + JSON.stringify(error));
    }

    function setPicture(fileUri) {
      imagePreview.setAttribute("style", "display:inline-block;");
      imagePreview.setAttribute("src", fileUri);
      enhancedImage = fileUri;
      pdfButton.disabled = false;
    }

    cameraButton.addEventListener("click", function() {
      cordova.plugins.GeniusScan.scanCamera(
        function onSuccess(scanUri) {
          setPicture(scanUri);
        },
        onError,
        bwFilterCheckbox.checked ? { defaultEnhancement: cordova.plugins.GeniusScan.ENHANCEMENT_BW } : {}
      );
    });

    imageButton.addEventListener("click", function() {
      cordova.plugins.GeniusScan.scanImage(
        cacheImgUri,
        function onSuccess(scanUri) {
          setPicture(scanUri);
        },
        onError,
        bwFilterCheckbox.checked ? { defaultEnhancement: cordova.plugins.GeniusScan.ENHANCEMENT_BW } : {}
      );
    });

    pdfButton.addEventListener("click", function() {
      cordova.plugins.GeniusScan.generatePDF(
        'Demo scan',
        [enhancedImage],
        function onSuccess(pdfUri) {
          // TODO: preview pdf ?
          alert('PDF generated at: ' + pdfUri);
          window.plugins.socialsharing.share("PDF", null, pdfUri);
        },
        onError,
        { password: 'test' }
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