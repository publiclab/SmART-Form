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
    var timeStart: Date?
    var timeEnd: Date?
    var before: UIImage?
    var after: UIImage?
    var temperature: String?
    var humidity: String?
    var state: Int?
    //MARK: Archiving Paths
    
    static let DocumentsDirectory = FileManager().urls(for: .documentDirectory, in: .userDomainMask).first!
    static let ArchiveURL = DocumentsDirectory.appendingPathComponent("tests")
    
    
    init?(id: String, title: String?, result: String?, date: Date?, timeStart: Date?, timeEnd: Date?, before: UIImage?, after: UIImage?,  temperature: String?, humidity: String?, state: Int?) {
        self.id = id
        self.title = title
        self.result = result
        self.date = date
        self.timeStart = timeStart
        self.timeEnd = timeEnd
        self.before = before
        self.after = after
        self.temperature = temperature
        self.humidity = humidity
        self.state = state
    }
    
    required convenience init?(coder aDecoder: NSCoder) {
        guard let id = aDecoder.decodeObject(forKey: "id") as? String else {
            print("Unable to decode the id for test.")
            return nil
        }
        
        let title = aDecoder.decodeObject(forKey: "title") as? String
        let result = aDecoder.decodeObject(forKey: "result") as? String
        let date = aDecoder.decodeObject(forKey: "date") as? Date
        let timeStart = aDecoder.decodeObject(forKey: "timeStart") as? Date
        let timeEnd = aDecoder.decodeObject(forKey: "timeEnd") as? Date
        let before = aDecoder.decodeObject(forKey: "before") as? UIImage
        let after = aDecoder.decodeObject(forKey: "after") as? UIImage
        let temperature = aDecoder.decodeObject(forKey: "temperature") as? String
        let humidity = aDecoder.decodeObject(forKey: "humidity") as? String
        let state = aDecoder.decodeObject(forKey: "state") as? Int
        
        // Must call designated initializer.
        self.init(id: id, title: title, result: result, date: date, timeStart: timeStart, timeEnd: timeEnd, before: before, after: after, temperature: temperature, humidity: humidity, state: state)
        
    }

    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "id")
        aCoder.encode(title, forKey: "title")
        aCoder.encode(result, forKey: "result")
        aCoder.encode(date, forKey: "date")
        aCoder.encode(timeStart, forKey: "timeStart")
        aCoder.encode(timeEnd, forKey: "timeEnd")
        aCoder.encode(before, forKey: "before")
        aCoder.encode(after, forKey: "after")
        aCoder.encode(temperature, forKey: "temperature")
        aCoder.encode(humidity, forKey: "humidity")
        aCoder.encode(state, forKey: "state")
    }
}
