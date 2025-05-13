import { scanDocument } from "@thegrizzlylabs/web-geniusscan-sdk";

// This code shows how to initialize the SDK with a license key.
//
//
// import { setLicenseKey } from "@thegrizzlylabs/web-geniusscan-sdk";
//
// setLicenseKey(<Your license key>).catch((e) => {
//   console.error(`Error setting Genius Scan SDK license key`, e);
// });

const scanButton = document.getElementById("scan");
const buttonText = scanButton.querySelector(".button-text");
const loadingSpinner = scanButton.querySelector(".loading-spinner");
const scannedImages = document.querySelector(".scanned-images");

function setLoading(isLoading) {
  scanButton.disabled = isLoading;
  buttonText.style.display = isLoading ? "none" : "inline";
  loadingSpinner.style.display = isLoading ? "inline" : "none";
}

scanButton.addEventListener("click", async () => {
  try {
    setLoading(true);
    const jpegImages = await scanDocument({ highlightColor: "orange" });

    scannedImages.innerHTML = "";

    let imageIndex = 1;
    for (const jpeg of jpegImages) {
      const url = URL.createObjectURL(jpeg);
      const a = document.createElement("a");
      a.href = url;
      a.download = `scanned-document-${imageIndex}.jpg`;
      a.title = "Click to download";
      const img = document.createElement("img");
      img.src = url;
      img.alt = "Scanned document";
      img.unload = () => {
        URL.revokeObjectURL(url);
      };
      a.appendChild(img);
      scannedImages.appendChild(a);
      imageIndex++;
    }
  } catch (error) {
    console.error(error);
    alert(`Error scanning document: ${error.message}`);
  } finally {
    setLoading(false);
  }
});
