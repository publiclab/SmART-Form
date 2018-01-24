//
//  Test.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/4/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//

import UIKit

class Test: NSObject, NSCoding{
    var id: String
    var title: String?
    var result: String?
    var date: Date?
    var image: UIImage?
    var temperature: String?
    var humidity: String?
    
    //MARK: Archiving Paths
    
    static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    static let ArchiveURL = DocumentsDirectory.appendingPathComponent("tests")
    
    
    init?(id: String, title: String?, result: String?, date: Date?, image: UIImage?, temperature: String?, humidity: String?) {
        self.id = id
        self.title = title
        self.result = result
        self.date = date
        self.image = image
        self.temperature = temperature
        self.humidity = humidity
    }
    
    required convenience init?(coder aDecoder: NSCoder) {
        guard let id = aDecoder.decodeObject(forKey: "id") as? String else {
            print("Unable to decode the id for test.")
            return nil
        }
        
        let title = aDecoder.decodeObject(forKey: "title") as? String
        let result = aDecoder.decodeObject(forKey: "result") as? String
        let date = aDecoder.decodeObject(forKey: "title") as? Date
        let image = aDecoder.decodeObject(forKey: "image") as? UIImage
        let temperature = aDecoder.decodeObject(forKey: "temperature") as? String
        let humidity = aDecoder.decodeObject(forKey: "humidity") as? String
        
        
        // Must call designated initializer.
        self.init(id: id, title: title, result: result, date: date, image: image, temperature: temperature, humidity: humidity)
        
    }

    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "id")
        aCoder.encode(title, forKey: "title")
        aCoder.encode(result, forKey: "result")
        aCoder.encode(date, forKey: "date")
        aCoder.encode(image, forKey: "image")
        aCoder.encode(temperature, forKey: "temperature")
        aCoder.encode(humidity, forKey: "humidity")

    }
}
