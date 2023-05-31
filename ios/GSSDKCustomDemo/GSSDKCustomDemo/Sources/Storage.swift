//
// Genius Scan SDK
//
// Copyright 2010-2020 The Grizzly Labs
//
// Subject to the Genius Scan SDK Licensing Agreement
// sdk@thegrizzlylabs.com
//



import Foundation

final class Storage {

    static let shared = Storage()
    private(set) var filePaths = [String]()

    private init() {}

    func addFile(_ filePath: String) {
        filePaths.append(filePath)
    }

}
