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
import SwiftUI

/**
 Entry view of the simple demo.

 If you are looking for how to integrate ScanFlow, either look at:
 - DocumentScanningViewController (UIKit integration, document scanning).
 - StructuredDataScanningView (SwiftUI integration, structured data extraction).
 */
struct MainView: View {
    var body: some View {
        NavigationView {
            List {
                Row(
                    title: "Document scanning",
                    subtitle: "Default scan flow",
                    imageName: "doc.viewfinder"
                ) {
                    DocumentScanningView()
                        .edgesIgnoringSafeArea(.all)
                }

                Row(
                    title: "Document scanning",
                    subtitle: "Customizable scan flow",
                    imageName: "doc.viewfinder.fill"
                ) {
                    CustomDocumentScanningView()
                }

                Row(
                    title: "Structured data scanning",
                    imageName: "creditcard.viewfinder"
                ) {
                    StructuredDataScanningView()
                }

                Row(
                    title: "Readable code scanning",
                    subtitle: "Barcodes and QR codes",
                    imageName: "qrcode.viewfinder"
                ) {
                    ReadableCodeScanningView()
                }
            }
            .navigationBarTitle("Genius Scan SDK Simple Demo", displayMode: .inline)
            .toolbar {
                ToolbarItemGroup(placement: .status) {
                    Text(appVersion)
                        .foregroundStyle(.secondary)
                }
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }

    private struct Row<DetailView: View>: View {
        var title: String
        var subtitle: String?
        var imageName: String

        @ViewBuilder var content: DetailView

        var body: some View {
            NavigationLink {
                content
                    .navigationBarTitle(title)
            } label: {
                HStack {
                    Image(systemName: imageName)
                    VStack(alignment: .leading) {
                        Text(title)
                        if let subtitle {
                            Text(subtitle).foregroundStyle(.gray).font(.caption)
                        }
                    }

                }
            }
        }
    }

    private var appVersion: String {
        guard let sdkVersion = Bundle(for: GSKScanFlow.self).infoDictionary?["CFBundleShortVersionString"] as? String,
              let appBuild = Bundle.main.infoDictionary?["CFBundleVersion"] as? String else {
            return "App version unavailable."
        }
        return "SDK: \(sdkVersion) • App build: \(appBuild)"
    }
}
