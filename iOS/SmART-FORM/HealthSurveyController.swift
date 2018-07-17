//
//  SampleData.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/13/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//


import UIKit

class HealthSurveyController: UIViewController {
    
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var idLabel: UILabel!
    @IBOutlet weak var dataSurvey: UIButton!
    var deviceID: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.addSubview(scrollView)
        
        deviceID = UserDefaults.standard.object(forKey: "deviceID") as? String
        idLabel.text = deviceID

    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
    }
    
    @IBAction func copyId(_ sender: Any) {
        UIPasteboard.general.string = deviceID
    }
    
    @IBAction func uploadData(_ sender: UIButton) {
        UIApplication.shared.openURL(NSURL(string: "https://osu.az1.qualtrics.com/jfe/form/SV_0xQggdNCixaXJwp")! as URL)
    }

}
