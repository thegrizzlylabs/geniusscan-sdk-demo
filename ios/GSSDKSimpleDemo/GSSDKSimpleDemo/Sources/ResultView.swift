//
// Genius Scan SDK
//
// Copyright 2010-2023 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import Foundation
import GSSDKScanFlow
import SwiftUI

/**
 This ResultView shows how you can display the results of a structured data extraction from ScanFlow.
 */
struct ResultView: View {
    var scan: GSKScanFlowScan
    var onRetry: () -> Void

    var body: some View {
        List {
            Section {
                if let image = UIImage(contentsOfFile: scan.enhancedFilePath) {
                    Image(uiImage: image).resizable().aspectRatio(contentMode: .fit)
                } else {
                    Text("Couldn't find enhanced image")
                }
            }

            Section("Structured data") {
                HStack {
                    Text("IBAN").fontWeight(.medium)
                    Text(scan.structuredDataResult?.bankDetails?.iban ?? "No IBAN detected")
                        .frame(maxWidth: .infinity, alignment: .trailing)
                }

                HStack {
                    Text("BICs").fontWeight(.medium)
                    Text(scan.structuredDataResult?.bankDetails?.bic ?? "No BIC detected")
                        .frame(maxWidth: .infinity, alignment: .trailing)
                }
            }

            Section {
                Button("Retry", action: onRetry)
            }
        }
    }
}
