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
    var newRequest: Int!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        browsingImage.image = newImage
        
        print("image view controller")
        print(newRequest)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "retakeImage" {
            let dvc = segue.destination as! CameraViewController
            dvc.newRequest = self.newRequest
        }
        else if segue.identifier == "saveImage" {
            print("savetoCamera")
            CustomPhotoAlbum.sharedInstance.save(image: self.newImage!)
        }
    }
    
    
}

