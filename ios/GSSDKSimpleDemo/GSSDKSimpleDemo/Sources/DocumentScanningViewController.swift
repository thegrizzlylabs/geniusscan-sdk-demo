//
// Genius Scan SDK
//
// Copyright 2010-2023 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import Foundation
import GSSDK
import UIKit

final class DocumentScanningViewController: UITableViewController, UIDocumentInteractionControllerDelegate {
    private var scanner: GSKScanFlow!

    private enum Row: Int {
        case camera
        case library
        case imageURL
    }

    init() {
        super.init(style: .insetGrouped)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "CellIdentifier")
    }

    override func numberOfSections(in tableView: UITableView) -> Int { 1 }

    override func tableView(_ tableView: UITableView, titleForFooterInSection section: Int) -> String? {
        """
        This view demonstrates how to start a scan flow in document scanning mode, \
        and how to integrate the scan flow with UIKit.
        """
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { 3 }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CellIdentifier", for: indexPath)
        cell.textLabel?.textColor = .systemBlue
        guard let row = Row(rawValue: indexPath.row) else { fatalError("Unexpected cell index") }
        switch row {
        case .camera:
            cell.textLabel?.text = "Scan from camera…"
        case .library:
            cell.textLabel?.text = "Import…"
        case .imageURL:
            cell.textLabel?.text = "Scan from URL…"
        }
        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let row = Row(rawValue: indexPath.row) else { fatalError("Unexpected cell index") }
        tableView.deselectRow(at: indexPath, animated: true)
        switch row {
        case .camera:
            scan(source: .camera)
        case .library:
            scan(source: .library)
        case .imageURL:
            scan(source: .imageURL)
        }
    }

    private func scan(source: GSKScanFlowSource) {
        // Create a configuration
        let configuration = GSKScanFlowConfiguration()
        configuration.source = source
        if source == .imageURL {
            configuration.sourceImageURL = URL(fileURLWithPath: Bundle.main.path(forResource: "image", ofType: "jpg")!)
        }
        configuration.backgroundColor = .white
        configuration.foregroundColor = .red
        configuration.multiPageFormat = .pdf
        let ocrConfiguration = GSKScanFlowOCRConfiguration()
        ocrConfiguration.languageTags = ["en-US"]
        configuration.ocrConfiguration = ocrConfiguration

        // Instantiate a scan flow with the configuration
        scanner = GSKScanFlow(configuration: configuration)
        scanner.start(from: self, onSuccess: { [weak self] result in
            /*
             Do what you need with the PDF. As an example, we display it:
             */
            if let multiPageDocumentURL = result.multiPageDocumentURL {
                print("Here is the document: \(multiPageDocumentURL)")
                let previewController = UIDocumentInteractionController(url: multiPageDocumentURL)
                previewController.delegate = self
                previewController.presentPreview(animated: true)
            }

            /*
             Alternatively, you could access the individual JPEG scans as follow:
             ```
             results.scans
             ```
             */
        }, failure: { [weak self] error in
            print("An error happened: \(error)")

            let alert = UIAlertController(
                title: error.localizedDescription,
                message: (error as NSError).localizedRecoverySuggestion,
                preferredStyle: .alert
            )
            alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
            self?.present(alert, animated: true, completion: nil)
        })
    }

    // MARK: - UIDocumentInteractionControllerDelegate

    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }
}
