function getCapacitor() {
  return window.Capacitor || null;
}

function getPlugin(name) {
  var capacitor = getCapacitor();
  if (!capacitor || !capacitor.Plugins) {
    return null;
  }

  return capacitor.Plugins[name] || null;
}

function normalizeError(error) {
  return {
    code: (error && error.data && error.data.code) || (error && error.code) || 'internal_error',
    message: (error && error.data && error.data.message) || (error && error.message) || '',
    recovery_message: (error && error.data && error.data.recovery_message) || '',
    underlying_error: (error && error.data && error.data.underlying_error) || '',
  };
}

function alertError(error) {
  alert('Error: ' + JSON.stringify(normalizeError(error), null, 2));
}

function guessMimeType(path) {
  return (path || '').toLowerCase().endsWith('.pdf') ? 'application/pdf' : undefined;
}

async function scanDocuments() {
  var geniusScan = getPlugin('GeniusScan');
  var fileOpener = getPlugin('FileOpener');

  if (!geniusScan) {
    throw new Error('GeniusScan plugin is unavailable.');
  }
  if (!fileOpener) {
    throw new Error('FileOpener plugin is unavailable.');
  }

  var result = await geniusScan.scanWithConfiguration({
    source: 'camera',
    multiPage: true,
    ocrConfiguration: {
      languages: ['en-US'],
    },
  });

  if (!result || !result.multiPageDocumentUrl) {
    throw new Error('The scan completed but no generated document was returned.');
  }

  await fileOpener.openFile({
    path: result.multiPageDocumentUrl,
    mimeType: guessMimeType(result.multiPageDocumentUrl),
  });
}

async function scanBarcodes() {
  var geniusScan = getPlugin('GeniusScan');
  if (!geniusScan) {
    throw new Error('GeniusScan plugin is unavailable.');
  }

  var result = await geniusScan.scanBarcodesWithConfiguration({
    isBatchModeEnabled: true,
    supportedCodeTypes: ['qr', 'code128', 'ean13'],
  });

  var codesText = (result && result.barcodes ? result.barcodes : []).map(function(code) {
    return code.type + ': ' + code.value;
  }).join('\n');

  alert('Detected codes:\n' + (codesText || 'No codes detected'));
}

async function runAction(action) {
  try {
    await action();
  } catch (error) {
    var payload = normalizeError(error);
    if (payload.code === 'cancellation_error') {
      return;
    }

    alertError(payload);
  }
}

function bindEvents() {
  // This code shows how to initialize the SDK with a license key.
  // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.
  //
  // getPlugin('GeniusScan').setLicenseKey({ licenseKey: '<Your license key>' });

  document.getElementById('scan-btn').addEventListener('click', function() {
    runAction(scanDocuments);
  });
  document.getElementById('barcode-btn').addEventListener('click', function() {
    runAction(scanBarcodes);
  });
}

document.addEventListener('DOMContentLoaded', bindEvents);
