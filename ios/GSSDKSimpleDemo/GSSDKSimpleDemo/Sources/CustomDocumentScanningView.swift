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
import SwiftUI

// - Demonstrates how to start a scan flow by directly calling `GSKScanFlow`.
// - Demonstrates the possible scan flow customizations.
struct CustomDocumentScanningView: View {
    @State private var scanFlow: GSKScanFlow?
    @State private var showAlert = false
    @State private var error: Error?
    @StateObject private var viewModel = DocumentScanningViewModel()
    
    // We use the document interaction controller to show the result of the scanning
    // but you likely don't need that in your app.
    @State private var documentInteractionControllerDelegate = DocumentInteractionControllerDelegate()

    var body: some View {
        Form {
            Text(
                    """
                    This view demonstrates the various customizations of the scan flow.

                    Check the GSKScanFlowConfiguration documentation for all of the possible customizations.
                    """
            )
            .listRowBackground(Color.clear)
            .font(.footnote)
            .foregroundColor(.gray)

            DocumentScanningConfigurationView(viewModel: viewModel)
        }
        .navigationBarItems(trailing: Button("Scan", action: scan))
        .alert(
            error?.localizedDescription ?? "",
            isPresented: $showAlert,
            actions: {
                Button("OK") { error = nil }
            },
            message: { 
                if let suggestion = (error as NSError?)?.localizedRecoverySuggestion {
                    Text(suggestion)
                }
            }
        )
    }

    private func scan() {
        guard let topViewController = UIApplication.shared.topViewController else {
            fatalError("No view controller to start scan flow from")
        }

        // Start the scan flow with a configuration. Here we retrieve the configuration
        // from the view model, but you probably want to hardcode it in your code.
        // Notice how we need to keep a strong reference on the scan flow.
        // You'd call the scan flow in the exact same way from UIKit.
        scanFlow = GSKScanFlow(configuration: viewModel.configuration)
        scanFlow?.start(from: topViewController, onSuccess: { result in
            /*
             Do what you need with the PDF. As an example, we display it:
             */
            if let multiPageDocumentURL = result.multiPageDocumentURL {
                print("Here is the document: \(multiPageDocumentURL)")
                let previewController = UIDocumentInteractionController(url: multiPageDocumentURL)
                previewController.delegate = documentInteractionControllerDelegate
                previewController.presentPreview(animated: true)
            }

            /*
             Alternatively, you could access the individual JPEG scans as follows:
             ```
             results.scans
             ```
             */
        }, failure: { error in
            print("An error happened: \(error)")

            self.showAlert = true
            self.error = error
        })
    }
}

private class DocumentInteractionControllerDelegate: NSObject, UIDocumentInteractionControllerDelegate {
    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        guard let topViewController = UIApplication.shared.topViewController else {
            fatalError("No view controller to present results from")
        }
        return topViewController
    }
}
