import GSSDK

/// The result of generating a page within a PDF Document.
struct PageGenerationResult: Identifiable {
    /// The identifier of the page. Unique within the context of a
    /// single instance of the app.
    let id: String
    /// A displayable title that represents the scanned page.
    var title: String
    /// An image that can be used to show a preview of the scanned page.
    var previewImage: UIImage
    /// Any structured data that was extracted from the scanned page.
    var structuredData: StructuredData?
}
