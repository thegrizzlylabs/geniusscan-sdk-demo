import GSSDK
import SwiftUI

/// View to display the results of readable code scanning
struct ReadableCodeResultsView: View {
    let result: GSKReadableCodeFlowResult
    let onRetry: () -> Void

    var body: some View {
        List {
            Section("Detected Codes") {
                ForEach(Array(result.readableCodes.reversed().enumerated()), id: \.offset) { index, code in
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
