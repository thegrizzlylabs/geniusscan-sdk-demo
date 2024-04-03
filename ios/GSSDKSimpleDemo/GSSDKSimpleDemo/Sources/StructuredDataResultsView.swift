//
// Genius Scan SDK
//
// Copyright 2010-2023 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import Contacts
import Foundation
import GSSDK
import SwiftUI

/**
 This view shows how you can display the results of a structured data extraction from ScanFlow.
 */
struct StructuredDataResultsView: View {
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

            if let result = scan.structuredDataResult {
                Section("Bank details") {
                    if let bankDetails = result.bankDetails {
                        Row(label: "IBAN", value: bankDetails.iban)
                        Row(label: "BIC", value: bankDetails.bic)
                    } else {
                        ContentUnavailableLabel(text: "No bank details detected")
                    }
                }

                Section("Business card contact") {
                    if let contact = result.businessCardContact {
                        Row(label: "Name", value: contact.name)
                        Row(label: "Phone number", value: contact.phoneNumbers.first?.number)
                        Row(label: "Email", value: contact.emailAddresses.first)
                    } else {
                        ContentUnavailableLabel(text: "No business card contact detected")
                    }
                }

                Section("Receipt") {
                    if let receipt = result.receipt {
                        Row(label: "Locale", value: receipt.locale?.identifier)
                        Row(label: "Merchant", value: receipt.merchant)
                        Row(label: "Amount", value: receipt.amount.flatMap(Self.numberFormatter.string))
                        Row(label: "Currency", value: receipt.currency)
                        Row(label: "Date", value: receipt.date.flatMap(Self.dateFormatter.string))
                        Row(label: "Category", value: receipt.category?.description.capitalized)
                    } else {
                        ContentUnavailableLabel(text: "No receipt detected")
                    }
                }
            } else {
                ContentUnavailableLabel(text: "No structured data detected")
            }

            Button("Retry", action: onRetry)
        }
    }
}

private extension StructuredDataResultsView {
    struct Row: View {
        var label: String
        var value: String?

        var body: some View {
            HStack {
                Text(label).fontWeight(.medium)

                if let value {
                    Text(value).frame(maxWidth: .infinity, alignment: .trailing)
                } else {
                    ContentUnavailableLabel(
                        text: emptyViewLabel,
                        alignment: .trailing
                    )
                }
            }
        }

        private var emptyViewLabel: String {
            let subject = if label.allSatisfy(\.isUppercase) {
                label
            } else {
                label.lowercased()
            }

            return "No \(subject) detected"
        }
    }

    struct ContentUnavailableLabel: View {
        var text: String
        var alignment = Alignment.center

        var body: some View {
            Text(text)
                .frame(maxWidth: .infinity, alignment: alignment)
                .foregroundStyle(.secondary)
        }
    }

    static let numberFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        formatter.alwaysShowsDecimalSeparator = true
        return formatter
    }()

    static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        return formatter
    }()
}
