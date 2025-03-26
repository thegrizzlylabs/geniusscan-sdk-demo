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

        Section("Enabled post-processing actions") {
            Toggle("Change filter", isOn: viewModel.bindingForPostProcessingAction(.editFilter))
            Toggle("Rotate", isOn: viewModel.bindingForPostProcessingAction(.rotate))
            Toggle("Correct distortion", isOn: viewModel.bindingForPostProcessingAction(.distortionCorrection))
        }

        Section("Output") {
            Toggle("Multipage", isOn: $viewModel.multiPage)

            Picker("Format", selection: $viewModel.multiPageFormat) {
                Text("PDF").tag(GSKScanFlowMultiPageFormat.PDF)
                Text("TIFF").tag(GSKScanFlowMultiPageFormat.TIFF)
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

private extension Binding where Value == UIColor {
    func uiColor() -> Binding<Color> {
        Binding<Color>(
            get: { Color(uiColor: self.wrappedValue) },
            set: { self.wrappedValue = UIColor($0) }
        )
    }
}

private extension Binding where Value == UIColor? {
    func uiColor(withDefault defaultColor: UIColor) -> Binding<Color> {
        Binding<Color>(
            get: { Color(uiColor: self.wrappedValue ?? defaultColor) },
            set: { self.wrappedValue = UIColor($0) }
        )
    }
}
