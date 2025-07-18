import Foundation
import GSSDK
import SwiftUI

/// - Demonstrates how to use GSKReadableCodeFlow for scanning barcodes and QR codes.
/// - Demonstrates how to present the readable code flow from SwiftUI.
struct ReadableCodeScanningView: View {
    @State private var scanResult: Result<GSKReadableCodeFlowResult, Error>?
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
                ReadableCodeResultsView(result: result, onRetry: clear)
            }
        } else {
            Form {
                Text("""
                    This view demonstrates how to scan readable codes (barcodes, QR codes) using the GSKReadableCodeFlow. 
                    
                    The flow supports these types of codes: \(GSKStructuredDataReadableCodeType.allCases.map(\.rawValue).sorted().joined(separator: ", "))
                    
                    Check the GSKReadableCodeFlowConfiguration documentation for all of the possible customizations.
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

    private func makeConfiguration() -> GSKReadableCodeFlowConfiguration {
        GSKReadableCodeFlowConfiguration(
            supportedCodeTypes: GSKStructuredDataReadableCodeType.allCases,
            isBatchModeEnabled: isBatchModeEnabled,
            highlightColor: highlightColor,
            menuColor: menuColor
        )
    }
}

private extension ReadableCodeScanningView {
    var batchModeDescription: String {
        if isBatchModeEnabled {
            return "Collects multiple codes over time, tap Done when finished"
        } else {
            return "Scans and returns all codes visible in one frame"
        }
    }

    func startScanning(with configuration: GSKReadableCodeFlowConfiguration) {
        Task {
            do {
                guard let topViewController = UIApplication.shared.topViewController else {
                    return
                }

                let readableCodeFlow = GSKReadableCodeFlow(configuration: configuration)
                let result = try await readableCodeFlow.resultByStarting(fromViewController: topViewController)

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
