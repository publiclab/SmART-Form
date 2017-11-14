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
        
    var test: Test! {
        didSet {
            titleLabel.text = test.title
        }
    }
}
