//
//  SampleData.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/13/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//


import UIKit

class ContactViewController: UIViewController {
    
    @IBOutlet weak var scrollView: UIScrollView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.addSubview(scrollView)
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        
        //scrollView.contentSize = CGSize(width: 320, height: 600)
    }
}

