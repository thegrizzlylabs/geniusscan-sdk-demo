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
    var originalImagePreview = document.getElementById("img_preview_original")
    var cameraButton = document.getElementById("scan_camera_btn");
    var imageButton = document.getElementById("scan_img_btn");
    var pdfButton = document.getElementById("scan_pdf_btn");
    var bwFilterCheckbox = document.getElementById("bw_filter");

    var originalImage;
    var enhancedImage;

    function onError(error) {
      alert("Error: " + JSON.stringify(error));
    }

    function setPictures(enhancedImageUri, originalImageUri) {
      originalImagePreview.setAttribute("style", "display:inline-block;");
      originalImagePreview.setAttribute("src", originalImageUri);
      originalImage = originalImageUri;
      imagePreview.setAttribute("style", "display:inline-block;");
      imagePreview.setAttribute("src", enhancedImageUri);
      enhancedImage = enhancedImageUri;
      pdfButton.disabled = false;
      imageButton.disabled = false;
    }

    cameraButton.addEventListener("click", function() {
      cordova.plugins.GeniusScan.scanCamera(
        function onSuccess(result) {
          setPictures(result['enhancedImageUri'], result['originalImageUri']);
        },
        onError,
        bwFilterCheckbox.checked ? { defaultEnhancement: cordova.plugins.GeniusScan.ENHANCEMENT_BW } : {}
      );
    });

    imageButton.addEventListener("click", function() {
      cordova.plugins.GeniusScan.scanImage(
        originalImage,
        function onSuccess(result) {
          setPictures(result['enhancedImageUri'], result['originalImageUri']);
        },
        onError,
        bwFilterCheckbox.checked ? { defaultEnhancement: cordova.plugins.GeniusScan.ENHANCEMENT_BW } : {}
      );
    });

    pdfButton.addEventListener("click", function() {
      cordova.plugins.GeniusScan.generatePDF(
        'Scan',
        [enhancedImage],
        function onSuccess(pdfUri) {
          // The file:// prefix is required to work on android, but iOS works with or without it
          var pdfPath = 'file://' + pdfUri;
          window.plugins.socialsharing.share("PDF", null, pdfPath);
        },
        onError,
        { /*password: 'test'*/ }
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
