//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

@preconcurrency import GSSDK
import SwiftUI
import UIKit

final class PDFViewController: UIViewController {

    @IBOutlet private(set) var titleField: UITextField!
    @IBOutlet private(set) var passwordField: UITextField!
    @IBOutlet private(set) var pageCountLabel: UILabel!
    @IBOutlet private(set) var ocrSwitch: UISwitch!
    @IBOutlet private(set) var readableCodesSwitch: UISwitch!
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
            let result = try await generatePDF()

            present(
                UINavigationController(rootViewController: UIHostingController(
                    rootView: ScanResultsView(
                        pageResults: result.pages,
                        onShowPDF: { [weak self] in
                            self?.dismiss(animated: true) {
                                self?.showPreviewController(forFileURL: result.fileURL)
                            }
                        },
                        onDismiss: { [weak self] in
                            self?.dismiss(animated: true)
                        }
                    )
                )),
                animated: true
            )
        }
    }

    // MARK: - Private

    /// This is where the PDF generation happens
    /// - First, create the information to generate the PDF document
    /// - Then generate the PDF document
    private func generatePDF() async throws -> GenerationResult {
        var ocrResults = [String: GSKOCRResult]()

        // Perform OCR if requested
        if ocrSwitch.isOn {
            let ocrConfiguration = GSKOCRConfiguration.configuration(languageTags: ["en-US"])

            for filePath in Storage.shared.filePaths {
                do {
                    let result = try await GSKOCR().recognizeText(forImageAtPath: filePath, ocrConfiguration: ocrConfiguration, onProgress: { @Sendable progress in
                        print("OCR engine progress: %f", progress)
                    })

                    ocrResults[filePath] = result
                } catch {
                    print("Error while OCR'ing page: \(error)")
                }
            }
        }

        var readableCodesResults = [String: [GSKStructuredDataReadableCode]]()
        if #available(iOS 17.0, *), readableCodesSwitch.isOn {
            for filePath in Storage.shared.filePaths {
                do {
                    let result = try await GSKReadableCodeDetector()
                        .detectReadableCodes(
                            inFileAt: URL(fileURLWithPath: filePath),
                            codeTypes: GSKStructuredDataReadableCodeType.allCases
                        )
                    readableCodesResults[filePath] = result
                } catch {
                    print("Error while OCR'ing page: \(error)")
                }
            }
        }

        // Prepare document for PDF generator

        // First we generate a list of pages.
        let pages = Storage.shared.filePaths.map { filePath -> GSKPDFPage in
            // For each page, we specify the document and a size in inches.
            GSKPDFPage(
                filePath: filePath,
                inchesSize: GSKPDFSize(width: 8.27, height: 11.69),
                textLayout: ocrResults[filePath]?.textLayout
            )
        }

        // We then create a GSKPDFDocument which holds the general information about the PDF document to generate
        let document = GSKPDFDocument(title: titleField.text, password: passwordField.text, keywords: nil, creationDate: Date(), lastUpdate: Date(), pages: pages)

        // Generate PDF

        // Last, we use the SDK to generate the actual PDF document
        let paths = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)
        let documentsDirectory = paths[0] as NSString
        let outputFilePath = documentsDirectory.appendingPathComponent("output.pdf")

        // Remove before creating.
        try? FileManager.default.removeItem(atPath: outputFilePath)

        do {
            try GSKDocumentGenerator().generate(document, configuration: .pdfConfiguration(withOutputFilePath: outputFilePath))

            var result = GenerationResult(fileURL: URL(fileURLWithPath: outputFilePath))

            for filePath in Storage.shared.filePaths {
                guard let previewImage = UIImage(contentsOfFile: filePath) else {
                    continue
                }

                try await result.pages.append(
                    PageGenerationResult(
                        id: filePath,
                        title: "Page \(result.pages.count + 1)",
                        previewImage: previewImage,
                        structuredData: makeStructuredData(
                            ocrResult: ocrResults[filePath],
                            readableCodes: readableCodesResults[filePath]
                        )
                    )
                )
            }

            return result
        } catch {
            print("Error while generating the PDF document: \(error)")
            throw error
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

private extension PDFViewController {
    struct GenerationResult {
        var fileURL: URL
        var pages = [PageGenerationResult]()
    }

    func makeStructuredData(
        ocrResult: GSKOCRResult?,
        readableCodes: [GSKStructuredDataReadableCode]?
    ) async throws -> StructuredData? {
        guard let ocrResult else { return nil }

        let dataExtractor = GSKStructuredDataExtractor()

        return try await StructuredData(
            bankDetails: dataExtractor.bankDetailsFromOCRResult(ocrResult),
            businessCardContact: dataExtractor.businessCardContactFromOCRResult(ocrResult),
            receipt: dataExtractor.receiptFromOCRResult(ocrResult),
            readableCodes: readableCodes ?? []
        )
    }

    func showPreviewController(forFileURL fileURL: URL) {
        previewController = UIDocumentInteractionController(url: fileURL)
        previewController?.delegate = self
        previewController?.presentPreview(animated: true)
    }
}
