import Foundation
import GSSDK
import SwiftUI

// This view is just a bridge between SwiftUI and UIKit.
//
// If you are implementing the ScanFlow with UIKit, you can simply look at
// `DocumentScanningViewController` which is self-contained.
struct DocumentScanningView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> DocumentScanningViewController {
        return DocumentScanningViewController()
    }

    func updateUIViewController(_ uiViewController: DocumentScanningViewController, context: Context) {
    }
}
