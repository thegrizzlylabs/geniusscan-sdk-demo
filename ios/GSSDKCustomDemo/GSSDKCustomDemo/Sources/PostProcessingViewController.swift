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
    private var enhancementBarButtonItem: UIBarButtonItem!
    private var imageView: UIImageView!
    private lazy var curvatureButton: UIButton = {
        let button = UIButton(type: .custom)
        button.setTitle("Correction", for: .normal)
        button.layer.cornerRadius = 4
        button.addTarget(self, action: #selector(correctCurvature), for: .touchUpInside)
        button.sizeToFit()
        return button
    }()
    private var filterType: GSKFilterType = .none
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

        imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        view.addSubview(imageView)

        imageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor)
        ])

        enhancementBarButtonItem = UIBarButtonItem(title: "Filter", style: .plain, target: self, action: #selector(edit))

        let correctionDistortionBarButtonItem = UIBarButtonItem(customView: curvatureButton)

        navigationItem.rightBarButtonItems = [
            UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(done)),
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

        alertController.addAction(UIAlertAction(title: NSLocalizedString("None", comment: ""), style: .default, handler: { _ in
            self.filterType = GSKFilterType.none
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Monochrome", comment: ""), style: .default, handler: { _ in
            self.filterType = GSKFilterType.monochrome
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Black & White", comment: ""), style: .default, handler: { _ in
            self.filterType = GSKFilterType.blackAndWhite
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Color", comment: ""), style: .default, handler: { _ in
            self.filterType = GSKFilterType.color
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Photo", comment: ""), style: .default, handler: { _ in
            self.filterType = GSKFilterType.photo
            self.processImage(autodetect: false)
        }))

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
        Storage.shared.addFile(currentProcessedImagePath!)
        let shareViewController = PDFViewController()
        navigationController?.pushViewController(shareViewController, animated: true)
    }

    /// This applies the powerful SDK image processing methods:
    /// - correct the perspective of the scan with the quadrangle set at the previous set
    /// - attempt to detect the best post-processing, or use the user defined post-processing
    /// - enhance the image according to this post-processing
    /// - optionally correct the image distortion (book/folded receipt curvature,).
    private func processImage(autodetect: Bool) {
        Task.detached(priority: .userInitiated) {
            let perspectiveCorrectionConfiguration = await GSKPerspectiveCorrectionConfiguration(quadrangle: self.quadrangle)
            let curvatureCorrectionConfiguration = await GSKCurvatureCorrectionConfiguration(curvatureCorrection: self.curvatureCorrectionEnabled)

            let enhancementConfiguration: GSKEnhancementConfiguration
            if autodetect {
                enhancementConfiguration = GSKEnhancementConfiguration.automatic()
            } else {
                enhancementConfiguration = await GSKEnhancementConfiguration(filter: self.filterType)
            }

            let result: GSKProcessingResult
            do {
                let configuration = GSKProcessingConfiguration(perspectiveCorrectionConfiguration: perspectiveCorrectionConfiguration,
                                                               curvatureCorrectionConfiguration: curvatureCorrectionConfiguration,
                                                               enhancementConfiguration: enhancementConfiguration,
                                                               rotationConfiguration: .automatic(),
                                                               outputConfiguration: .png())
                result = try await GSKScanProcessor().processImage(self.scan.image, configuration: configuration)
            } catch {
                print("Error while processing scan: \(error)")
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
                self.refreshImageView()
            }
        }
    }

    private func refreshImageView() {
        if let currentProcessedImagePath = currentProcessedImagePath {
            imageView.image = UIImage(contentsOfFile: currentProcessedImagePath)
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
