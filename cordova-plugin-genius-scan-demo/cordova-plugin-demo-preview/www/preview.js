var exec = require("cordova/exec");

function PreviewAnyFile() {}

PreviewAnyFile.prototype.previewPath = function(successCallback, errorCallback, path, opt) {
  var options = opt || {};
  var name = options.name || "";
  var mimeType = options.mimeType || "";
  exec(successCallback, errorCallback, "PreviewAnyFile", "previewPath", [path, name, mimeType]);
};

module.exports = new PreviewAnyFile();
