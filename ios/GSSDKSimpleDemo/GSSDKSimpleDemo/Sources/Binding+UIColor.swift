import SwiftUI

extension Binding where Value == UIColor {
    func uiColor() -> Binding<Color> {
        Binding<Color>(
            get: { Color(uiColor: self.wrappedValue) },
            set: { self.wrappedValue = UIColor($0) }
        )
    }
}

extension Binding where Value == UIColor? {
    func uiColor(withDefault defaultColor: UIColor) -> Binding<Color> {
        Binding<Color>(
            get: { Color(uiColor: self.wrappedValue ?? defaultColor) },
            set: { self.wrappedValue = UIColor($0) }
        )
    }
}
