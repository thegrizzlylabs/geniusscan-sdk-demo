//
//  GSSDKSimpleDemo
//
//  Created by Bruno Virlet on 23/01/2024.
//  Copyright © 2024 The Grizzly Labs. All rights reserved.
//

import Foundation
import GSSDK
import SwiftUI

@dynamicMemberLookup
final class DocumentScanningViewModel: ObservableObject {
    @Published var configuration = GSKScanFlowConfiguration()

    init() {
        configuration.sourceImageURL = Bundle.main.url(forResource: "bank-identity-document", withExtension: "jpg")
    }

    subscript<T>(dynamicMember keyPath: WritableKeyPath<GSKScanFlowConfiguration, T>) -> T {
        get { configuration[keyPath: keyPath] }
        set { configuration[keyPath: keyPath] = newValue }
    }

    func bindingForReadabilityEnabled() -> Binding<Bool> {
        Binding(get: {
            self.requiredReadabilityLevel > .lowest
        }, set: { isEnabled in
            self.requiredReadabilityLevel = isEnabled ? .high : .lowest
        })
    }

    func bindingForPostProcessingAction(_ action: GSKScanFlowPostProcessingActions) -> Binding<Bool> {
        Binding(get: {
            self.postProcessingActions.contains(action)
        }, set: { enabled in
            var postProcessingActions = self.postProcessingActions
            if enabled {
                postProcessingActions.insert(action)
            } else {
                postProcessingActions.remove(action)
            }
            self.postProcessingActions = postProcessingActions
        })
    }

    func bindingForPDFMaxScanDimensionEnabled() -> Binding<Bool> {
        Binding(get: {
            self.pdfMaxScanDimension != 0
        }, set: { enabled in
            self.pdfMaxScanDimension = enabled ? 1000 : 0
        })
    }

    func bindingForPDFMaxScanDimension() -> Binding<Double> {
        Binding(get: {
            Double(self.pdfMaxScanDimension)
        }, set: { value in
            self.pdfMaxScanDimension = Int(value)
        })
    }

    func bindingForOCR() -> Binding<Bool> {
        Binding(get: {
            self.ocrConfiguration != nil
        }, set: { enabled in
            if enabled {
                let ocrConfiguration = GSKScanFlowOCRConfiguration()
                ocrConfiguration.languageTags = ["en-US"]
                self.ocrConfiguration = ocrConfiguration
            } else {
                self.ocrConfiguration = nil
            }
        })
    }

    func bindingForJPEGQuality() -> Binding<Float> {
        Binding(get: {
            Float(self.jpegQuality)
        }, set: { quality in
            self.jpegQuality = Int(quality)
        })
    }
}
