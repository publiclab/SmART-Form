//
//  TestCell.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/13/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//

import UIKit

class TestCell: UITableViewCell {
    
    @IBOutlet var titleLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var resultLabel: UILabel!
    @IBOutlet weak var imageLabel: UIImageView!
    @IBOutlet weak var unitType: UILabel!
    @IBOutlet weak var progressLabel: UILabel!
    
    var test: Test! {
        didSet {
            titleLabel.text = test.title
            timeLabel.text = test.date?.toString(dateFormat: "MMMdd,HH:mm")
            if(test.state) == 1 {
                resultLabel.text = test.timeEnd?.toString(dateFormat: "MMMdd,HH:mm")
                imageLabel.backgroundColor = UIColor.yellow
                progressLabel.text = "In-progress"
                unitType.text = "end time"
            }
            else if(test.state) == 2 {
                resultLabel.text = test.result
                imageLabel.backgroundColor = UIColor.orange
                progressLabel.text = "Completed"
                unitType.text = "ppb"
            }
        }
    }
}
