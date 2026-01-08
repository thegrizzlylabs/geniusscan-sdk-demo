import GSSDK

/// Type that encapsulates all structured data that was
/// extracted from a scanned document.
struct StructuredData {
    /// Any bank details that were extracted.
    var bankDetails: GSKStructuredDataBankDetails?
    /// Any contact that was extracted from a business card.
    var businessCardContact: GSKStructuredDataContact?
    /// Any receipt/invoice info that was extracted.
    var receipt: GSKStructuredDataReceipt?
    /// Any barcodes
    var barcodes: [GSKBarcode] = []
}
