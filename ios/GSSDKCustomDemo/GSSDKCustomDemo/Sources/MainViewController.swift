//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//

import UIKit

final class MainViewController: UIViewController {
    private lazy var activityIndicator: UIActivityIndicatorView = {
        let indicator = UIActivityIndicatorView(style: .large)
        indicator.translatesAutoresizingMaskIntoConstraints = false
        indicator.hidesWhenStopped = true
        return indicator
    }()

    private lazy var errorLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        label.numberOfLines = 0
        label.textColor = .red
        label.isHidden = true
        return label
    }()

    override func viewDidLoad() {
        super.viewDidLoad()

        view.addSubview(activityIndicator)
        view.addSubview(errorLabel)

        NSLayoutConstraint.activate([
            activityIndicator.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            activityIndicator.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            errorLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            errorLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            errorLabel.leadingAnchor.constraint(equalTo: view.layoutMarginsGuide.leadingAnchor),
            errorLabel.trailingAnchor.constraint(equalTo: view.layoutMarginsGuide.trailingAnchor)
        ])
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        activityIndicator.startAnimating()

        Task {
            do {
                let cameraViewController = try await CameraViewController()
                showCameraViewController(cameraViewController)
            } catch {
                showError(error)
            }
        }
    }

    private func showCameraViewController(_ cameraViewController: CameraViewController) {
        activityIndicator.stopAnimating()

        addChild(cameraViewController)
        view.addSubview(cameraViewController.view)
        cameraViewController.view.frame = view.bounds
        cameraViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        cameraViewController.didMove(toParent: self)
    }

    private func showError(_ error: Error) {
        activityIndicator.stopAnimating()
        errorLabel.text = "Failed to initialize camera: \(error.localizedDescription)"
        errorLabel.isHidden = false
    }
}
