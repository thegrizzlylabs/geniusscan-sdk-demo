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

    override func viewDidLoad() {
        super.viewDidLoad()

        do {
            try GSK.initWithLicenseKey("<YOUR LICENSE KEY - MAKE SURE TO CHANGE YOUR BUNDLE ID TO MATCH THE KEY - WITHOUT LICENSE KEY THE DEMO WILL RUN FOR ONLY 60 SECONDS>")
        } catch {
            /**
             If the SDK is not properly initialized, the SDK method will return errors. This will help you setup the SDK properly. After that, the only reason why this may return an error would be if the license expires.
             All the SDK errors return proper errors in that case and you can handle them to ensure you provide a good "degraded" experience. For instance, you can prompt them to update the application to use the scanning
             feature.
             */
            NSLog("Error while initializing the Genius Scan SDK: \(error)")
        }
    }

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
        return configuration;
    }

    private func scan(with configuration: GSKScanFlowConfiguration) {
        scanner = GSKScanFlow(configuration: configuration)
        scanner.start(from: self, onSuccess: { [weak self] result in

            NSLog("Here is the PDF file: \(result.pdfURL)")

            /**
             Do what you need with the PDF. As an example, we display it:
             */

            let previewController = UIDocumentInteractionController(url: result.pdfURL)
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
