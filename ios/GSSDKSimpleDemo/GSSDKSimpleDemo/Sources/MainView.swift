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
 Entry view of the simple demo.

 If you are looking for how to integrate ScanFlow, either look at:
 - DocumentScanningViewController (UIKit integration, document scanning).
 - StructuredDataScanningView (SwiftUI integration, structured data extraction).
 */
struct MainView: View {
    var body: some View {
        NavigationView {
            List {
                NavigationLink {
                    DocumentScanningView()
                        .edgesIgnoringSafeArea(.all)
                        .navigationBarTitle("Document scanning")
                } label: {
                    HStack {
                        Image(systemName: "doc.viewfinder")
                        Text("Document scanning")
                    }
                }

                NavigationLink {
                    StructuredDataScanningView()
                        .navigationBarTitle("Structured data scanning")
                } label: {
                    Image(systemName: "creditcard.viewfinder")
                    Text("Structured data scanning")
                }
            }
            .navigationBarTitle("Genius Scan SDK Simple Demo", displayMode: .inline)
        }
    }
}
