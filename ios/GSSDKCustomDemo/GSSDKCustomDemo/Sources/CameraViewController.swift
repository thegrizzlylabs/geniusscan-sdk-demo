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
import AVFoundation

/**
 A very simple camera view.

 The complexity is hidden in GSKCameraViewController.

 You can plug into the delegate methods of the GSKCameraViewControllerDelegate to customize
 its behavior, in particular what you want to do when a scan has been captured.

 You customize the camera interface by subclassing the GSKCameraViewController.
The GSKCameraViewController doesn't expose any UI aside from the camera preview, so you
 are free to add your own. In this example here we had a bottom "toolbar" that contains
 a camera button to snap the photo.
 */
final class CameraViewController: GSKCameraViewController {
    private lazy var toolbar: UIView = {
        let toolbar = UIView()
        toolbar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(toolbar)
        return toolbar
    }()
    private lazy var toggleAutoModeButton: UIButton = {
        let autoModeButton = UIButton()
        autoModeButton.setTitle(NSLocalizedString("Auto", comment: ""), for: .normal)
        autoModeButton.translatesAutoresizingMaskIntoConstraints = false
        autoModeButton.addTarget(self, action: #selector(toggleAutoMode), for: .touchUpInside)
        toolbar.addSubview(autoModeButton)
        return autoModeButton
    }()
    private lazy var cameraButton: UIButton = {
        let cameraButton = UIButton()
        cameraButton.setTitle(NSLocalizedString("Snap", comment: ""), for: .normal)
        cameraButton.translatesAutoresizingMaskIntoConstraints = false
        cameraButton.addTarget(self, action: #selector(takePhoto), for: .touchUpInside)
        toolbar.addSubview(cameraButton)
        return cameraButton
    }()
    private lazy var userGuidanceLabel: UILabel = {
        let userGuidanceLabel = UILabel()
        userGuidanceLabel.translatesAutoresizingMaskIntoConstraints = false
        userGuidanceLabel.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        userGuidanceLabel.textColor = UIColor.white
        userGuidanceLabel.textAlignment = NSTextAlignment.center
        userGuidanceLabel.adjustsFontSizeToFitWidth = true
        view.addSubview(userGuidanceLabel)
        return userGuidanceLabel
    }()

    init() async throws {
        try await super.init(configuration: .documentDetection())
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setupConstraints()

        self.delegate = self
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.isNavigationBarHidden = true
    }

    @objc func toggleAutoMode() {
        documentDetectionMode = documentDetectionMode == .highlightAndAutoCapture ? .highlightOnly : .highlightAndAutoCapture
        toggleAutoModeButton.setTitle(documentDetectionMode == .highlightAndAutoCapture ? NSLocalizedString("Auto", comment: "") : NSLocalizedString("Manual", comment: ""), for: .normal)
    }
}

extension CameraViewController: GSKCameraViewControllerDelegate {
    /**
     We just received a photo from the camera. We could do some post-processing immediately but here we choose to immediately show the interface that lets the user edit the crop area.
     */
    func cameraViewController(_ cameraViewController: GSKCameraViewController, didGenerateScan scan: GSKScan) {
        // We re-enable the camera button
        cameraButton.isEnabled = true

        // … and we let the user edit the frame of the detected document.
        let editFrameViewController = EditFrameViewController(scan: scan)
        navigationController?.pushViewController(editFrameViewController, animated: true)
    }

    func cameraViewControllerFailedToFindDocument(_ cameraViewController: GSKCameraViewController) {
        showUserGuidance(with: NSLocalizedString("Searching for document…", comment: ""))
        removePulseAnimation()
    }

    func cameraViewController(_ cameraViewController: GSKCameraViewController, didFindDocumentWithQuadrangle quadrangle: GSKQuadrangle) {
        showUserGuidance(with: NSLocalizedString("Document found. Remain steady.", comment: ""))
    }

    func cameraViewControllerPhotoStabilizationDidStart(_ cameraViewController: GSKCameraViewController) {
        // We are indicating to the user that the photo will be taken momentarily by
        // making the shutter button pulse.
        addPulseAnimation()
    }

    func cameraViewController(_ cameraViewController: GSKCameraViewController, willSnapPhotoWithQuadrangle quadrangle: GSKQuadrangle) {
        // For instance, here we disable the camera button so that the user doesn't take multiple photos at the same time.
        cameraButton.isEnabled = false
        removePulseAnimation()
    }

    // MARK: - Private

    private func addPulseAnimation() {
        guard cameraView.layer.animation(forKey: "pulse") == nil else {
            return
        }

        let pulseAnimation = CABasicAnimation(keyPath: "opacity")
        pulseAnimation.duration = 0.1
        pulseAnimation.repeatCount = .greatestFiniteMagnitude
        pulseAnimation.autoreverses = true
        pulseAnimation.fromValue = 1
        pulseAnimation.toValue = 0
        cameraButton.layer.add(pulseAnimation, forKey: "pulse")
    }

    private func removePulseAnimation() {
        cameraButton.layer.removeAnimation(forKey: "pulse")
    }

    private func showUserGuidance(with message: String?) {
        userGuidanceLabel.text = message
        userGuidanceLabel.isHidden = message == nil
    }

    private func setupConstraints() {
        cameraView.translatesAutoresizingMaskIntoConstraints = false

        let topMargin: CGFloat = 40
        let bottomToolbarHeight: CGFloat = 124

        NSLayoutConstraint.activate([
            cameraView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            cameraView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            cameraView.topAnchor.constraint(equalTo: view.topAnchor, constant: topMargin),
            cameraView.bottomAnchor.constraint(equalTo: toolbar.topAnchor),
            toolbar.heightAnchor.constraint(equalToConstant: bottomToolbarHeight),
            toolbar.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            toolbar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            toolbar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            cameraButton.centerXAnchor.constraint(equalTo: toolbar.centerXAnchor),
            cameraButton.centerYAnchor.constraint(equalTo: toolbar.centerYAnchor),
            toggleAutoModeButton.leadingAnchor.constraint(equalToSystemSpacingAfter: toolbar.leadingAnchor, multiplier: 1),
            toggleAutoModeButton.centerYAnchor.constraint(equalTo: toolbar.centerYAnchor),
            userGuidanceLabel.centerXAnchor.constraint(equalTo: cameraView.centerXAnchor),
            userGuidanceLabel.leadingAnchor.constraint(equalTo: cameraView.layoutMarginsGuide.leadingAnchor),
            toolbar.topAnchor.constraint(equalToSystemSpacingBelow: userGuidanceLabel.bottomAnchor, multiplier: 1)
        ])
    }

}
