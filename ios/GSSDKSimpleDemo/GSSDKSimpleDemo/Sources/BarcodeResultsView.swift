import GSSDK
import SwiftUI

/// View to display the results of barcode scanning
struct BarcodeResultsView: View {
    let result: GSKBarcodeScanFlowResult
    let onRetry: () -> Void

    var body: some View {
        List {
            Section("Detected Codes") {
                ForEach(Array(result.barcodes.reversed().enumerated()), id: \.offset) { index, code in
                    HStack {
                        Text(code.value)
                            .monospacedDigit()

                        Spacer()
                        Text(code.type.rawValue.capitalized)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .padding(.vertical, 2)
                }
            }

            Section {
                Button("Scan Another Code", action: onRetry)
            }
        }
    }
}
