import GSSDK
import SwiftUI

/// View that shows any structured data that was extracted
/// from a scanned document.
struct StructuredDataResultsView: View {
    var data: StructuredData

    var body: some View {
        List {
            Section(title: "Bank details") {
                if let bankDetails = data.bankDetails {
                    Row(label: "IBAN", value: bankDetails.iban)
                    Row(label: "BIC", value: bankDetails.bic)
                } else {
                    ContentUnavailableLabel(text: "No bank details detected")
                }
            }

            Section(title: "Business card contact") {
                if let contact = data.businessCardContact {
                    Row(label: "Name", value: contact.name)
                    Row(label: "Phone number", value: contact.phoneNumbers.first?.number)
                    Row(label: "Email", value: contact.emailAddresses.first)
                } else {
                    ContentUnavailableLabel(text: "No business card contact detected")
                }
            }

            Section(title: "Receipt") {
                if let receipt = data.receipt {
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

            Section(title: "Readable codes") {
                ForEach(data.readableCodes, id: \.self) { readableCode in
                    Row(label: readableCode.type.rawValue, value: readableCode.value)
                }
            }
        }
        .navigationBarTitle("Structured data")
    }
}

private extension StructuredDataResultsView {
    struct Section<Content: View>: View {
        var title: String
        @ViewBuilder var content: () -> Content

        var body: some View {
            SwiftUI.Section(content: content, header: {
                Text(title)
            })
        }
    }

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
                .foregroundColor(.secondary)
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
