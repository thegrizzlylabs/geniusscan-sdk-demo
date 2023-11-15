//
//  ViewController.swift
//  GSSDKScannerDemo
//
//  Created by Bruno Virlet on 5/7/19.
//  Copyright © 2019 The Grizzly Labs. All rights reserved.
//

import Foundation
import GSSDKScanFlow

final class ViewController: UIViewController, UIDocumentInteractionControllerDelegate {
    private var scanner: GSKScanFlow!

    @objc @IBAction func scan(_ sender: UIButton) {
        let configuration = baseScannerConfiguration()
        configuration.source = .camera

        scan(with: configuration)
    }

    @objc @IBAction func library(_ sender: Any) {
        let configuration = baseScannerConfiguration()
        configuration.source = .library

        scan(with: configuration)
    }

    @objc @IBAction func imageURL(_ sender: Any) {
        let configuration = baseScannerConfiguration()
        configuration.source = .imageURL
        configuration.sourceImageURL = URL(fileURLWithPath: Bundle.main.path(forResource: "image", ofType: "jpg")!)

        scan(with: configuration)
    }
    
    private func baseScannerConfiguration() -> GSKScanFlowConfiguration {
        let configuration = GSKScanFlowConfiguration()
        configuration.backgroundColor = .white
        configuration.foregroundColor = .red
        configuration.multiPageFormat = .PDF
        let ocrConfiguration = GSKScanFlowOCRConfiguration()
        ocrConfiguration.languageCodes = ["eng"]
        ocrConfiguration.trainedDataPath = (Bundle.main.resourcePath! as NSString).appendingPathComponent("tessdata")
        configuration.ocrConfiguration = ocrConfiguration
        return configuration;
    }

    private func scan(with configuration: GSKScanFlowConfiguration) {
        scanner = GSKScanFlow(configuration: configuration)
        scanner.start(from: self, onSuccess: { [weak self] result in

            NSLog("Here is the document: \(result.multiPageDocumentURL)")

            /**
             Do what you need with the PDF. As an example, we display it:
             */

            let previewController = UIDocumentInteractionController(url: result.multiPageDocumentURL!)
            previewController.delegate = self
            previewController.presentPreview(animated: true)

            /**
             Alternatively, you could access the individual JPEG scans as follow:
             ```
             results.scans
             ```
             */
        }, failure: { [weak self] error in
            NSLog("An error happened: \(error)")

            let alert = UIAlertController(title: error.localizedDescription, message: nil, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .cancel, handler: nil))
            self?.present(alert, animated: true, completion: nil)
        })
    }

    // MARK: - UIDocumentInteractionControllerDelegate

    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }
}
