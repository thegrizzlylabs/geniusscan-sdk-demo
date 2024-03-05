import Contacts
import Foundation
import GSSDK
import SwiftUI

/// View that shows the results of a scanning session.
struct ScanResultsView: View {
    /// The results for each page that was scanned.
    var pageResults: [PageGenerationResult]
    /// A closure that's run when the user asked to be shown the full
    /// generated PDF (including all pages).
    var onShowPDF: () -> Void
    /// A closure that's run when the user dismissed the view.
    var onDismiss: () -> Void

    var body: some View {
        List {
            Section(content: {
                ForEach(pageResults) { pageResult in
                    if let structuredData = pageResult.structuredData {
                        NavigationLink(destination: {
                            StructuredDataResultsView(data: structuredData)
                        }, label: {
                            Row(pageResult: pageResult)
                        })
                    } else {
                        Row(pageResult: pageResult)
                    }
                }
            }, header: {
                Text("Pages")
            })
        }
        .navigationBarItems(
            leading: Button("Show PDF", action: onShowPDF),
            trailing: Button("Done", action: onDismiss)
        )
        .navigationBarTitle("Scan results")
    }
}

private extension ScanResultsView {
    struct Row: View {
        var pageResult: PageGenerationResult

        var body: some View {
            HStack(spacing: 12) {
                Image(uiImage: pageResult.previewImage)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(maxHeight: 44)

                Text(pageResult.title)
            }
        }
    }
}
