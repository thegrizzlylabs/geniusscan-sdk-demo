import { scanWithConfiguration } from "@thegrizzlylabs/web-geniusscan-sdk";

// This code shows how to initialize the SDK with a license key.
//
//
// import { setLicenseKey } from "@thegrizzlylabs/web-geniusscan-sdk";
//
// setLicenseKey(<Your license key>).catch((e) => {
//   console.error(`Error setting Genius Scan SDK license key`, e);
// });

const scanButton = document.getElementById("scan");
const scannedImages = document.querySelector(".scanned-images");
const pdfDownload = document.querySelector(".pdf-download");

scanButton.addEventListener("click", async () => {
  try {
    const { scans, multiPageDocument } = await scanWithConfiguration({
      highlightColor: "orange",
      multiPageFormat: "pdf",
      multiPage: true,
    });

    scannedImages.innerHTML = "";
    pdfDownload.innerHTML = "";

    let imageIndex = 1;
    for (const scan of scans) {
      const url = URL.createObjectURL(scan.enhancedImage.data);
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

    if (multiPageDocument) {
      const blob = new Blob([multiPageDocument.data], { type: multiPageDocument.type });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.textContent = "Download PDF";
      a.download = `${Date.now()}-scanned-documents`;
      pdfDownload.appendChild(a);
    }
  } catch (error) {
    console.error(error);
    alert(`Error scanning document: ${error.message}`);
  }
});
