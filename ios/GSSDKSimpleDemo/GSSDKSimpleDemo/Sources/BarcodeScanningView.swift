import Foundation
import GSSDK
import SwiftUI

/// - Demonstrates how to use GSKBarcodeScanFlow for scanning barcodes and QR codes.
/// - Demonstrates how to present the barcode scan flow from SwiftUI.
struct BarcodeScanningView: View {
    @State private var scanResult: Result<GSKBarcodeScanFlowResult, Error>?
    @State private var isPresenting = false
    @State private var isBatchModeEnabled: Bool = false
    @State private var menuColor: UIColor?
    @State private var highlightColor: UIColor = .green

    var body: some View {
        if let result = scanResult {
            switch result {
            case .failure(let error):
                List {
                    Section("Error") {
                        Text(error.localizedDescription)
                    }

                    Section {
                        Button(action: clear) {
                            Text("Retry")
                        }
                    }
                }
            case .success(let result):
                BarcodeResultsView(result: result, onRetry: clear)
            }
        } else {
            Form {
                Text("""
                    This view demonstrates how to scan barcodes (including QR codes) using the GSKBarcodeScanFlow. 
                    
                    The flow supports these types of codes: \(GSKBarcodeType.allCases.map(\.rawValue).sorted().joined(separator: ", "))
                    
                    Check the GSKBarcodeScanFlowConfiguration documentation for all of the possible customizations.
                    """
                )
                .listRowBackground(Color.clear)
                .font(.footnote)
                .foregroundColor(.gray)

                makeConfigurationView()
            }
            .toolbar {
                ToolbarItemGroup(placement: .topBarTrailing) {
                    Button("Scan", action: scan)
                }
            }
        }
    }

    private func makeConfigurationView() -> some View {
        List {
            Section {
                Toggle("Enable Batch Mode", isOn: $isBatchModeEnabled)
            } footer: {
                Text(batchModeDescription)
            }

            Section("UI") {
                ColorPicker("Highlight color", selection: $highlightColor.uiColor())
                ColorPicker("Menu color", selection: $menuColor.uiColor(withDefault: .tintColor))
            }
        }
    }

    private func scan() {
        startScanning(with: makeConfiguration())
    }

    private func makeConfiguration() -> GSKBarcodeScanFlowConfiguration {
        GSKBarcodeScanFlowConfiguration(
            supportedCodeTypes: GSKBarcodeType.allCases,
            isBatchModeEnabled: isBatchModeEnabled,
            highlightColor: highlightColor,
            menuColor: menuColor
        )
    }
}

private extension BarcodeScanningView {
    var batchModeDescription: String {
        if isBatchModeEnabled {
            return "Collects multiple codes over time, tap Done when finished"
        } else {
            return "Scans and returns all codes visible in one frame"
        }
    }

    func startScanning(with configuration: GSKBarcodeScanFlowConfiguration) {
        Task {
            do {
                guard let topViewController = UIApplication.shared.topViewController else {
                    return
                }

                let barcodeScanFlow = GSKBarcodeScanFlow(configuration: configuration)
                let result = try await barcodeScanFlow.resultByStarting(fromViewController: topViewController)

                await MainActor.run {
                    self.scanResult = .success(result)
                }
            } catch {
                await MainActor.run {
                    self.scanResult = .failure(error)
                }
            }
        }
    }

    func clear() {
        scanResult = nil
    }
}
