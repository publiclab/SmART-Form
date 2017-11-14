//
//  Test.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/4/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//

import UIKit

class Test: NSObject, NSCoding{
    var id: String?
    var title: String?
    var result: String?
    var date: Date
    var image: UIImage?
    var temperature: String?
    var humidity: String?
    
    init(id: String?, title: String?, result: String?, date: Date, image: UIImage?, temperature: String?, humidity: String?) {
        self.id = id
        self.title = title
        self.result = result
        self.date = date
        self.image = image
        self.temperature = temperature
        self.humidity = humidity
    }
    
    required init(coder aDecoder: NSCoder) {
        self.id = aDecoder.decodeObject(forKey: "id") as? String
        self.title = aDecoder.decodeObject(forKey: "title") as? String
        self.result = aDecoder.decodeObject(forKey: "result") as? String
        self.date = aDecoder.decodeObject(forKey: "date") as! Date
        self.image = aDecoder.decodeObject(forKey: "image") as? UIImage
        self.temperature = aDecoder.decodeObject(forKey: "temperature") as? String
        self.humidity = aDecoder.decodeObject(forKey: "humidity") as? String
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
