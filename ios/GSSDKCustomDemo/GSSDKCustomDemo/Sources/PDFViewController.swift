//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import GSSDK
import UIKit

final class PDFViewController: UIViewController {

    @IBOutlet private(set) var titleField: UITextField!
    @IBOutlet private(set) var passwordField: UITextField!
    @IBOutlet private(set) var pageCountLabel: UILabel!
    @IBOutlet private(set) var ocrSwitch: UISwitch!
    private var previewController: UIDocumentInteractionController?

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        pageCountLabel.text = String(format: NSLocalizedString("%d pages", comment: ""), Storage.shared.filePaths.count)
    }

    // MARK: - Actions

    @IBAction func addAnotherPage(_ sender: Any) {
        navigationController?.popToRootViewController(animated: true)
    }

    @IBAction func share(_ sender: Any) {
        Task {
            guard let url = await generatePDF(nil) else {
                print("Cannot generate url")
                return
            }

            await MainActor.run {
                previewController = UIDocumentInteractionController(url: url)
                previewController?.delegate = self
                previewController?.presentPreview(animated: true)
            }
        }
    }

    // MARK: - Private

    /// This is where the PDF generation happens
    /// - First, create the information to generate the PDF document
    /// - Then generate the PDF document
    private func generatePDF(_ ocrResult: GSKOCRResult?) async -> URL? {
        var textLayouts = [String: GSKTextLayout]()

        // Perform OCR if requested
        if ocrSwitch.isOn {
            let ocrConfiguration: GSKOCRConfiguration = .configuration(languageTags: ["en-US"])

            for filePath in Storage.shared.filePaths {
                do {
                    let result = try await GSKOCR().recognizeText(forImageAtPath: filePath, ocrConfiguration: ocrConfiguration, onProgress: { progress in
                        print("OCR engine progress: %f", progress)
                    })

                    textLayouts[filePath] = result.textLayout
                } catch {
                    print("Error while OCR'ing page: \(error)")
                }
            }
        }

        // Prepare document for PDF generator

        // First we generate a list of pages.
        let pages = Storage.shared.filePaths.map { filePath -> GSKPDFPage in
            // For each page, we specify the document and a size in inches.
            let page = GSKPDFPage(filePath: filePath, inchesSize: GSKPDFSize(width: 8.27, height: 11.69), textLayout: textLayouts[filePath])
            return page
        }

        // We then create a GSKPDFDocument which holds the general information about the PDF document to generate
        let document = GSKPDFDocument(title: titleField.text, password: passwordField.text, keywords: nil, creationDate: Date(), lastUpdate: Date(), pages: pages)

        // Generate PDF

        // Last, we use the SDK to generate the actual PDF document
        let paths = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)
        let documentsDirectory = paths[0] as NSString
        let outputFilePath = documentsDirectory.appendingPathComponent("output.pdf")

        do {
            // Remove before creating.
            try FileManager.default.removeItem(atPath: outputFilePath)
        } catch {
            // Swallow error
        }

        do {
            try GSKPDF.generate(document, toPath: outputFilePath)
            return URL(fileURLWithPath: outputFilePath)
        } catch {
            print("Error while generating the PDF document: \(error)")
            return nil
        }
    }

}

extension PDFViewController: UIDocumentInteractionControllerDelegate {

    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }

}

extension PDFViewController: UITextFieldDelegate {

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

}
