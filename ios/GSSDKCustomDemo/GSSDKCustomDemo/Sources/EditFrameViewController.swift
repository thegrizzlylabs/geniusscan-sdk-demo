//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import UIKit
@preconcurrency import GSSDK

final class EditFrameViewController: GSKEditFrameViewController {

    let scan: GSKScan
    let documentDetector: GSKDocumentDetector

    init(scan: GSKScan) {
        self.scan = scan
        self.documentDetector = GSKDocumentDetector()

        super.init(image: scan.image, quadrangle: .full())
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .black
        frameView.magnifierView.centerStyle = .quadrangle
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: true)
        navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(done))

        print("Detecting quadrangle…")

        // We use the SDK method to autodetect the quadrangle and show the result to the user
        Task.detached(priority: .userInitiated) {
            var result: GSKDocumentDetectionResult?

            do {
                result = try await self.documentDetector.detectDocument(in: self.image)
            } catch {
                print("Error while detecting document frame: \(error)")
                result = nil
            }

            await MainActor.run {
                print("Quadrangle analysis finished. Found: \(String(describing: result?.quadrangle))")

                // We update the display quadrangle
                if let quadrangle = result?.quadrangle, !quadrangle.isEmpty() {
                    self.quadrangle = quadrangle
                } else {
                    self.quadrangle = GSKQuadrangle.full()
                }
            }
        }
    }

    @objc private func done() {
        let postProcessingViewController = PostProcessingViewController(scan: scan, quadrangle: quadrangle)
        navigationController?.pushViewController(postProcessingViewController, animated: true)
    }

}
