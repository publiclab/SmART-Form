//
//  ImageViewController.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 1/23/18.
//  Copyright © 2018 Siyang Zhang. All rights reserved.
//

import UIKit
import Foundation

class ImageViewController : UIViewController {
    @IBOutlet weak var browsingImage: UIImageView!
    var newImage: UIImage!

    override func viewDidLoad() {
        super.viewDidLoad()
        browsingImage.image = newImage
    }
}
