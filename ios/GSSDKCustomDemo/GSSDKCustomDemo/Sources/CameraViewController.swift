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

/**
 A very simple camera view.

 The complexity is hidden in GSKCameraViewController.
 You have access to all the delegate methods of the GSKCameraSession.

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
        captureView.addSubview(userGuidanceLabel)
        return userGuidanceLabel
    }()

    override func viewDidLoad() {
        super.viewDidLoad()

        setupConstraints()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.isNavigationBarHidden = true
    }

    // MARK: - Camera Session Delegate overrides

    /**
     We use the delegate methods of the GSKCameraSession to react to the different states of taking the photo.
     */
    override nonisolated func cameraSessionWillSnapPhoto(_ cameraSession: GSKCameraSession) {
        super.cameraSessionWillSnapPhoto(cameraSession)

        // For instance, here we disable the camera button so that the user doesn't take multiple photos at the same time.
        Task { @MainActor in
            cameraButton.isEnabled = false
        }
    }

    /**
     We just received a photo from the camera. We could do some post-processing immediately but here we choose to immediately show the interface that lets the user edit the crop area.
     */
    override nonisolated func cameraSession(_ cameraSession: GSKCameraSession, didGenerateScan scan: GSKScan) {
        super.cameraSession(cameraSession, didGenerateScan: scan)

        Task { @MainActor in
            // We re-enable the camera button
            cameraButton.isEnabled = true

            // … and we let the user edit the frame of the detected document.
            let editFrameViewController = EditFrameViewController(scan: scan)
            navigationController?.pushViewController(editFrameViewController, animated: true)
        }
    }

    override nonisolated func cameraSessionFailedToFindQuadrangle(_ cameraSession: GSKCameraSession) {
        super.cameraSessionFailedToFindQuadrangle(cameraSession)

        Task { @MainActor in
            showUserGuidance(with: NSLocalizedString("Searching for document…", comment: ""))
            removePulseAnimation()
        }
    }

    override nonisolated func cameraSession(_ cameraSession: GSKCameraSession, didFindQuadrangle quadrangle: GSKQuadrangle) {
        super.cameraSession(cameraSession, didFindQuadrangle: quadrangle)

        Task { @MainActor in
            showUserGuidance(with: NSLocalizedString("Document found. Remain steady.", comment: ""))
        }
    }

    override nonisolated func cameraSessionIsAboutToChooseQuadrangle(_ cameraSession: GSKCameraSession) {
        super.cameraSessionIsAboutToChooseQuadrangle(cameraSession)

        Task { @MainActor in
            // We are indicating to the user that the photo will be taken momentarily by
            // making the shutter button pulse.
            addPulseAnimation()
        }
    }

    override nonisolated func cameraSession(_ cameraSession: GSKCameraSession, willAutoTriggerWithQuadrangle quadrangle: GSKQuadrangle) {
        super.cameraSession(cameraSession, willAutoTriggerWithQuadrangle: quadrangle)

        Task { @MainActor in
            removePulseAnimation()
        }
    }

    // MARK: - Private

    private func addPulseAnimation() {
        guard captureView.layer.animation(forKey: "pulse") == nil else {
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
        captureView.translatesAutoresizingMaskIntoConstraints = false

        let topMargin: CGFloat = 40
        let bottomToolbarHeight: CGFloat = 124

        NSLayoutConstraint.activate([
            captureView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            captureView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            captureView.topAnchor.constraint(equalTo: view.topAnchor, constant: topMargin),
            captureView.bottomAnchor.constraint(equalTo: toolbar.topAnchor),
            toolbar.heightAnchor.constraint(equalToConstant: bottomToolbarHeight),
            toolbar.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            toolbar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            toolbar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            cameraButton.centerXAnchor.constraint(equalTo: toolbar.centerXAnchor),
            cameraButton.centerYAnchor.constraint(equalTo: toolbar.centerYAnchor),
            userGuidanceLabel.centerXAnchor.constraint(equalTo: captureView.centerXAnchor),
            userGuidanceLabel.leadingAnchor.constraint(equalTo: captureView.layoutMarginsGuide.leadingAnchor),
            toolbar.topAnchor.constraint(equalToSystemSpacingBelow: userGuidanceLabel.bottomAnchor, multiplier: 1)
        ])
    }

}
