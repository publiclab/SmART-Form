//
//  ConsentViewController.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 8/22/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//

import UIKit
import Foundation


class ConsentViewController: UIViewController {    
    @IBOutlet weak var scrollView: UIScrollView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(scrollView)
    }
    
    @IBAction func disagreeConsent(_ sender: Any) {
        exit(0);
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        scrollView.contentSize = CGSize(width: 320, height: 2000)
    }
}
