import Foundation
import GSSDK
import SwiftUI

/// - Demonstrates a configuration for structured data scanning.
/// - Demonstrates how to present the scan flow from SwiftUI with `GSKScanFlowButton`.
struct StructuredDataScanningView: View {
    @State private var scanFlowResult: Result<GSKScanFlowScan, Error>?

    var body: some View {
        if let result = scanFlowResult {
            switch result {
            case .failure(let error):
                List {
                    Section {
                        Text(error.localizedDescription)
                    }

                    Section {
                        Button(action: clear) {
                            Text("Retry")
                        }
                    }
                }
            case .success(let scan):
                StructuredDataResultsView(scan: scan, onRetry: clear)
            }
        } else {
            List {
                Section(
                    footer: Text("This view demonstrates how to start a scan flow in structured data scanning extraction mode, and how to integrate the scan flow with the SwiftUI view GSKScanFlowButton.")
                ) {
                    GSKScanFlowButton(
                        "Scan with camera",
                        configuration: {
                            scanConfiguration(forSource: .camera)
                        },
                        action: handleScanResult
                    )

                    GSKScanFlowButton(
                        "Import…",
                        configuration: {
                            scanConfiguration(forSource: .library)
                        },
                        action: handleScanResult
                    )

                    GSKScanFlowButton(
                        "Scan from URL…",
                        configuration: {
                            scanConfiguration(forSource: .imageURL)
                        },
                        action: handleScanResult
                    )
                }
            }
        }
    }
}

private extension StructuredDataScanningView {
    func scanConfiguration(
        forSource source: GSKScanFlowSource
    ) -> GSKScanFlowConfiguration {
        let configuration = GSKScanFlowConfiguration()
        configuration.source = source

        if source == .imageURL {
            configuration.sourceImageURL = URL(fileURLWithPath: Bundle.main.path(forResource: "bank-identity-document", ofType: "jpg")!)
        }

        configuration.multiPage = false
        configuration.skipPostProcessingScreen = true
        configuration.structuredData = [.bankDetails, .businessCard, .receipt, .readableCode]

        return configuration
    }

    func handleScanResult(_ result: Result<GSKScanFlowResult, Error>) {
        switch result {
        case .success(let result):
            guard let scan = result.scans.first else { return }
            scanFlowResult = .success(scan)
        case .failure(let error):
            scanFlowResult = .failure(error)
        }
    }

    func clear() {
        scanFlowResult = nil
    }
}
