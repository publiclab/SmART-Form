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
            timeLabel.text = test.date?.toString(dateFormat: "MMM dd, yyyy HH:mm")
            resultLabel.text = test.result
            if test.state == 1{
                imageLabel.backgroundColor = UIColor.yellow
                progressLabel.text = "In-progress"
                unitType.text = "remaining"
            } else if test.state == 2{
                imageLabel.backgroundColor = UIColor.orange
                progressLabel.text = "Completed"
                unitType.text = "ppb"
            }
        }
    }
}
