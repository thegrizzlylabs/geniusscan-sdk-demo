// We use https://cordova.apache.org/docs/en/latest/reference/cordova-plugin-file to copy
// a sample image to scan with our demo app.
function copy(filepath, toDirectory, filename) {
  window.resolveLocalFileSystemURL(filepath, function(fileEntry) {
    window.resolveLocalFileSystemURL(toDirectory, function(dirEntry) {
      dirEntry.getFile(filename, {create: true, exclusive: false}, function(targetFileEntry) {
        fileEntry.file(function(file) {
          targetFileEntry.createWriter(function (fileWriter) {
            fileWriter.write(file);
          });
        })
      })
    })
  })
}

var app = {
  // Application Constructor
  initialize: function() {
    document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
  },

  // deviceready Event Handler
  //
  // Bind any cordova events here. Common events are:
  // 'pause', 'resume', etc.
  onDeviceReady: function() {
    this.receivedEvent('deviceready');

    var assetImgUri = `${cordova.file.applicationDirectory}www/img/scan.jpg`
    var cacheImgUri = `${cordova.file.cacheDirectory}scan.jpg`
    // On Android, we can't can't manipulate an assset file file:///android_asset/xxxx
    // as a normal file uri (https://stackoverflow.com/questions/4820816/how-to-get-uri-from-an-asset-file)
    // so we first need to copy that file in cache...
    copy(assetImgUri, cordova.file.cacheDirectory, 'scan.jpg')

    function onError(message) {
      alert('Error: ' + message)
    }

    function setPicture(id, fileUri) {
      var img = document.getElementById(id);
      img.setAttribute('style', 'display:inline-block;');
      img.setAttribute('src', fileUri);
    }

    var cameraButton = document.getElementById('scan_camera_btn');
    cameraButton.addEventListener('click', function() {
      cordova.plugins.GeniusScan.scanCamera(
        function onSuccess(scanUri) {
          setPicture('after', scanUri)
        },
        onError
      );
    });

    var imageButton = document.getElementById('scan_img_btn');
    imageButton.addEventListener('click', function() {
      cordova.plugins.GeniusScan.scanImage(
        cacheImgUri,
        function onSuccess(scanUri) {
          setPicture('after', scanUri)
        },
        onError
      );
    });
  },

  // Update DOM on a Received Event
  receivedEvent: function(id) {
    var parentElement = document.getElementById(id);
    var listeningElement = parentElement.querySelector('.listening');
    var receivedElement = parentElement.querySelector('.received');

    listeningElement.setAttribute('style', 'display:none;');
    receivedElement.setAttribute('style', 'display:block;');

    console.log('Received Event: ' + id);
  }
};

app.initialize();
