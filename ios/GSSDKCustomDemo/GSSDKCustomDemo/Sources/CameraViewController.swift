//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import UIKit
import GSSDK

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
        cameraView.addSubview(userGuidanceLabel)
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
    override func cameraSessionWillSnapPhoto(_ cameraSession: GSKCameraSession) {
        super.cameraSessionWillSnapPhoto(cameraSession)

        // For instance, here we disable the camera button so that the user doesn't take multiple photos at the same time.
        DispatchQueue.main.async {
            self.cameraButton.isEnabled = false
        }
    }

    /**
     We just received a photo from the camera. We could do some post-processing immediately but here we choose to immediately show the interface that lets the user edit the crop area.
     */
    override func cameraSession(_ cameraSession: GSKCameraSession, didGenerate scan: GSKScan) {
        super.cameraSession(cameraSession, didGenerate: scan)

        DispatchQueue.main.async {
            // We re-enable the camera button
            self.cameraButton.isEnabled = true

            // … and we let the user edit the frame of the detected document.
            let editFrameViewController = EditFrameViewController(scan: scan)
            self.navigationController?.pushViewController(editFrameViewController, animated: true)
        }
    }

    override func cameraSessionFailed(toFindQuadrangle cameraSession: GSKCameraSession) {
        super.cameraSessionFailed(toFindQuadrangle: cameraSession)

        userGuidance(with: NSLocalizedString("Searching for document…", comment: ""))
        removePulseAnimation()
    }

    override func cameraSession(_ cameraSession: GSKCameraSession, didFind quadrangle: GSKQuadrangle) {
        super.cameraSession(cameraSession, didFind: quadrangle)

        userGuidance(with: NSLocalizedString("Document found. Remain steady.", comment: ""))
    }

    override func cameraSessionIsAbout(toChooseQuadrangle cameraSession: GSKCameraSession) {
        super.cameraSessionIsAbout(toChooseQuadrangle: cameraSession)

        // We are indicating to the user that the photo will be taken momentarily by
        // making the shutter button pulse.
        addPulseAnimation()
    }

    override func cameraSession(_ cameraSession: GSKCameraSession, willAutoTriggerWith quadrangle: GSKQuadrangle) {
        super.cameraSession(cameraSession, willAutoTriggerWith: quadrangle)

        removePulseAnimation()
    }

    // MARK: - Private

    private func addPulseAnimation() {
        DispatchQueue.main.async {
            guard self.captureView.layer.animation(forKey: "pulse") == nil else {
                return
            }

            let pulseAnimation = CABasicAnimation(keyPath: "opacity")
            pulseAnimation.duration = 0.1
            pulseAnimation.repeatCount = .greatestFiniteMagnitude
            pulseAnimation.autoreverses = true
            pulseAnimation.fromValue = 1
            pulseAnimation.toValue = 0
            self.cameraButton.layer.add(pulseAnimation, forKey: "pulse")
        }
    }

    private func removePulseAnimation() {
        DispatchQueue.main.async {
            self.cameraButton.layer.removeAnimation(forKey: "pulse")
        }
    }

    private func userGuidance(with message: String?) {
        DispatchQueue.main.async {
            self.userGuidanceLabel.text = message
            self.userGuidanceLabel.isHidden = message == nil
        }
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
            userGuidanceLabel.centerXAnchor.constraint(equalTo: cameraView.centerXAnchor),
            userGuidanceLabel.leadingAnchor.constraint(equalTo: cameraView.layoutMarginsGuide.leadingAnchor),
            toolbar.topAnchor.constraint(equalToSystemSpacingBelow: userGuidanceLabel.bottomAnchor, multiplier: 1)
        ])
    }

}
