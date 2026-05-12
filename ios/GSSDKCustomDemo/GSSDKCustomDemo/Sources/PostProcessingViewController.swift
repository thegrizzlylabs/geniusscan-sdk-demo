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

final class PostProcessingViewController: UIViewController {

    let scan: GSKScan
    let quadrangle: GSKQuadrangle
    private var currentProcessedImagePath: String?
    private lazy var imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()
    private lazy var enhancementBarButtonItem = UIBarButtonItem(title: "Filter", style: .plain, target: self, action: #selector(edit))
    private lazy var doneBarButtonItem: UIBarButtonItem = {
        let item = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(done))
        item.isEnabled = false
        return item
    }()
    private lazy var curvatureButton: UIButton = {
        let button = UIButton(type: .custom)
        button.setTitle("Correction", for: .normal)
        button.layer.cornerRadius = 4
        button.addTarget(self, action: #selector(correctCurvature), for: .touchUpInside)
        button.sizeToFit()
        return button
    }()
    private var enhancementConfiguration: GSKEnhancementConfiguration = .automatic()
    private var curvatureCorrectionEnabled = false

    init(scan: GSKScan, quadrangle: GSKQuadrangle) {
        self.scan = scan
        self.quadrangle = quadrangle

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .black

        view.addSubview(imageView)

        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor)
        ])

        let correctionDistortionBarButtonItem = UIBarButtonItem(customView: curvatureButton)

        navigationItem.rightBarButtonItems = [
            doneBarButtonItem,
            correctionDistortionBarButtonItem,
            enhancementBarButtonItem
        ]
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        refreshImageView()
        configureCurvatureButtonColor()
        processImage(autodetect: true)
    }

    // MARK: - Private

    @objc private func edit() {
        let alertController = UIAlertController(title: NSLocalizedString("Choose a filter to apply", comment: ""), message: nil, preferredStyle: .actionSheet)
        alertController.popoverPresentationController?.barButtonItem = enhancementBarButtonItem

        FilterOption.allCases.forEach { option in
            alertController.addAction(UIAlertAction(title: option.title, style: .default, handler: { _ in
                self.enhancementConfiguration = option.configuration
                self.processImage(autodetect: false)
            }))
        }

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: ""), style: .cancel, handler: { _ in
            //
        }))

        present(alertController, animated: true, completion: nil)
    }

    @objc private func correctCurvature() {
        curvatureCorrectionEnabled.toggle()
        configureCurvatureButtonColor()
        processImage(autodetect: false)
    }

    @objc private func done() {
        guard let currentProcessedImagePath else {
            assertionFailure("Done tapped but no processed image available.")
            return
        }

        Storage.shared.addFile(currentProcessedImagePath)
        let shareViewController = PDFViewController()
        navigationController?.pushViewController(shareViewController, animated: true)
    }

    /// This applies the powerful SDK image processing methods:
    /// - correct the perspective of the scan with the quadrangle set at the previous set
    /// - attempt to detect the best post-processing, or use the user defined post-processing
    /// - enhance the image according to this post-processing
    /// - optionally correct the image distortion (book/folded receipt curvature,).
    private func processImage(autodetect: Bool) {
        doneBarButtonItem.isEnabled = false
        Task.detached(priority: .userInitiated) {
            let perspectiveCorrectionConfiguration = GSKPerspectiveCorrectionConfiguration(quadrangle: self.quadrangle)
            let curvatureCorrectionConfiguration = await GSKCurvatureCorrectionConfiguration(curvatureCorrection: self.curvatureCorrectionEnabled)

            let enhancementConfiguration: GSKEnhancementConfiguration = if autodetect {
                .automatic()
            } else {
                await self.enhancementConfiguration
            }

            let result: GSKProcessingResult
            do {
                let configuration = GSKProcessingConfiguration(
                    perspectiveCorrectionConfiguration: perspectiveCorrectionConfiguration,
                    curvatureCorrectionConfiguration: curvatureCorrectionConfiguration,
                    enhancementConfiguration: enhancementConfiguration,
                    rotationConfiguration: .automatic(),
                    readabilityConfiguration: .default(),
                    outputConfiguration: .png()
                )
                result = try await GSKScanProcessor().processImage(self.scan.image, configuration: configuration)
            } catch {
                print("Error while processing scan: \(error)")
                await MainActor.run {
                    self.currentProcessedImagePath = nil
                    self.refreshImageView()
                    self.doneBarButtonItem.isEnabled = false
                }
                return
            }

            if let currentProcessedImagePath = await self.currentProcessedImagePath {
                do {
                    try FileManager.default.removeItem(atPath: currentProcessedImagePath)
                } catch {
                    print("Unable to remove previous enhanced file")
                }
            }

            await MainActor.run {
                self.currentProcessedImagePath = result.processedImagePath
                self.doneBarButtonItem.isEnabled = true
                self.refreshImageView()
            }
        }
    }

    private func refreshImageView() {
        imageView.image = if let currentProcessedImagePath {
            UIImage(contentsOfFile: currentProcessedImagePath)
        } else {
            nil
        }
    }

    private func configureCurvatureButtonColor() {
        let blueColor = UIColor(red: 0, green: 122/255, blue: 1, alpha: 1)
        if curvatureCorrectionEnabled {
            curvatureButton.layer.backgroundColor = blueColor.cgColor
            curvatureButton.setTitleColor(.white, for: .normal)
        } else {
            curvatureButton.layer.backgroundColor = UIColor.clear.cgColor
            curvatureButton.setTitleColor(blueColor, for: .normal)
        }
    }
}

private extension PostProcessingViewController {
    enum FilterOption: CaseIterable {
        case none, monochrome, blackAndWhite, color, photo

        var title: String {
            switch self {
            case .none:
                NSLocalizedString("None", comment: "")
            case .monochrome:
                NSLocalizedString("Monochrome", comment: "")
            case .blackAndWhite:
                NSLocalizedString("Black & White", comment: "")
            case .color:
                NSLocalizedString("Color", comment: "")
            case .photo:
                NSLocalizedString("Photo", comment: "")
            }
        }

        var configuration: GSKEnhancementConfiguration {
            switch self {
            case .none: .fixed(filterConfiguration: .noOp)
            case .monochrome: .automatic(filterStyle: .document, colorPalette: .monochrome)
            case .blackAndWhite: .automatic(filterStyle: .document, colorPalette: .grayscale)
            case .color: .automatic(filterStyle: .document, colorPalette: .color)
            case .photo: .fixed(filterConfiguration: .photo)
            }
        }
    }

}
