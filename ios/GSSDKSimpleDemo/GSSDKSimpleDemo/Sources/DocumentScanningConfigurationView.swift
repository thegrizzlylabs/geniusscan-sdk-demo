//
//  GSSDKSimpleDemo
//
//  Created by Bruno Virlet on 23/01/2024.
//  Copyright © 2024 The Grizzly Labs. All rights reserved.
//

import Foundation
import SwiftUI
import GSSDK

// A view that lets you toggle the scan flow configuration in the UI.
//
// You don't need this view when using the actual Scan Flow demo.
struct DocumentScanningConfigurationView: View {
    @ObservedObject var viewModel: DocumentScanningViewModel

    var body: some View {
        Section {
            Picker("Source", selection: $viewModel.source) {
                ForEach(GSKScanFlowSource.allCases, id: \.self) { source in
                    Text(source.description).tag(source)
                }
            }
        }

        Section("Camera screen") {
            Toggle("Display photo library button", isOn: Binding(get: {
                !viewModel.photoLibraryButtonHidden
            }, set: { displayed in
                viewModel.photoLibraryButtonHidden = !displayed
            }))

            Toggle("Display flash button", isOn: Binding(get: {
                !viewModel.flashButtonHidden
            }, set: { displayed in
                viewModel.flashButtonHidden = !displayed
            }))

            Picker("Default flash mode", selection: $viewModel.defaultFlashMode) {
                ForEach(GSKScanFlowFlashMode.allCases, id: \.self) { mode in
                    Text(mode.description).tag(mode)
                }
            }
        }

        Picker("Default scan orientation", selection: $viewModel.configuration.defaultScanOrientation) {
            ForEach(GSKScanOrientation.allCases, id: \.self) { orientation in
                Text(orientation.description).tag(orientation)
            }
        }

        Picker("Default filter", selection: $viewModel.defaultFilter) {
            ForEach(GSKScanFlowFilterType.allCases, id: \.self) { type in
                Text(type.description).tag(type)
            }
        }

        Toggle("Detect readability", isOn: viewModel.bindingForReadabilityEnabled())

        Toggle("Default curvature correction", isOn: Binding(get: {
            viewModel.defaultCurvatureCorrectionMode == .enabled
        }, set: { isOn in
            viewModel.defaultCurvatureCorrectionMode = isOn ? .enabled : .disabled
        }))

        Section(content: {
            Picker("Perform validation", selection: $viewModel.showCropValidation) {
                Text("Never").tag(GSKCropValidation.never)
                Text("Always").tag(GSKCropValidation.always)
                Text("Confidence-based").tag({ () -> GSKCropValidation in
                    switch viewModel.showCropValidation {
                    case .always, .never:
                        .whenConfidenceBelowOrEqual(.lowest)
                    case .whenConfidenceBelowOrEqual:
                        viewModel.showCropValidation
                    @unknown default:
                        .whenConfidenceBelowOrEqual(.lowest)
                    }
                }())
            }

            if case GSKCropValidation.whenConfidenceBelowOrEqual = viewModel.showCropValidation {
                Picker("Confidence threshold", selection: $viewModel.showCropValidation) {
                    ForEach(GSKAutoCropConfidenceLevel.allCases, id: \.self) { level in
                        Text(level.rawValue.capitalized).tag(GSKCropValidation.whenConfidenceBelowOrEqual(level))
                    }
                }
            }
        }, header: {
            Text("Auto-crop validation")
        }, footer: {
            if case GSKCropValidation.whenConfidenceBelowOrEqual = viewModel.showCropValidation {
                Text("Perform validation if the auto-crop confidence is below or equal to the selected threshold.")
            }
        })

        Section("Post-processing screen") {
            Toggle(
                "Show post-processing screen",
                isOn: Binding(
                    get: { !viewModel.skipPostProcessingScreen
                    },
                    set: { show in
                        viewModel.skipPostProcessingScreen = !show
                    }
                )
            )
        }

        Section("Final review screen") {
            Toggle("Show final review screen", isOn: $viewModel.showFinalReview)
        }

        if !viewModel.skipPostProcessingScreen {
            Section("Enabled post-processing actions") {
                Toggle("Change filter", isOn: viewModel.bindingForPostProcessingAction(.editFilter))
                Toggle("Rotate", isOn: viewModel.bindingForPostProcessingAction(.rotate))
                Toggle("Correct distortion", isOn: viewModel.bindingForPostProcessingAction(.distortionCorrection))
            }
        }

        Section("Output") {
            Button(action: {
                viewModel.shouldPresentOutputFolderPicker = true
            }, label: {
                HStack {
                    Text("Folder")
                        .foregroundStyle(Color.primary)
                    Spacer(minLength: 100)
                    Text(viewModel.configuration.outputDirectoryURL.relativePath)
                        .lineLimit(1)
                        .truncationMode(.head)
                        .foregroundStyle(Color.secondary)
                }
            })

            Toggle("Multipage", isOn: $viewModel.multiPage)

            Picker("Format", selection: $viewModel.multiPageFormat) {
                Text("PDF").tag(GSKScanFlowMultiPageFormat.pdf)
                Text("TIFF").tag(GSKScanFlowMultiPageFormat.tiff)
            }

            if viewModel.multiPageFormat == .pdf {
                SecureField("PDF password", text: viewModel.bindingForPDFPassword())
            }

            Toggle("Resize scans in PDF", isOn: viewModel.bindingForPDFMaxScanDimensionEnabled())

            if viewModel.pdfMaxScanDimension != 0 {
                HStack {
                    Text("Max size")
                    Slider(value: viewModel.bindingForPDFMaxScanDimension(), in: 0...10000.0)
                    Text("\(viewModel.pdfMaxScanDimension)")
                }
            }

            Picker("Page size", selection: $viewModel.pdfPageSize) {
                Text("Fit").tag(GSKScanFlowPDFPageSize.fit)
                Text("Letter").tag(GSKScanFlowPDFPageSize.letter)
                Text("A4").tag(GSKScanFlowPDFPageSize.A4)
            }

            HStack {
                Text("JPEG Quality")
                Slider(value: viewModel.bindingForJPEGQuality(), in: 0...100)
                Text("\(viewModel.jpegQuality)")
            }
        }

        Section("UI") {
            ColorPicker("Foreground color", selection: $viewModel.foregroundColor.uiColor())
            ColorPicker("Background color", selection: $viewModel.backgroundColor.uiColor())
            ColorPicker("Highlight color", selection: $viewModel.highlightColor.uiColor())
            ColorPicker("Menu color", selection: $viewModel.menuColor.uiColor(withDefault: .tintColor))
        }

        Section("OCR") {
            Toggle("OCR", isOn: viewModel.bindingForOCR())
        }
    }
}
