//
//  TestsTableViewController.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/4/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//

import UIKit
//import Eula

class TestsTableViewController: UITableViewController {
    
    var tests = [Test]()
    let identifier = "testCell"

    @IBOutlet weak var noTest: UILabel!
    @IBOutlet weak var healthBtn: UIButton!
    @IBOutlet weak var userBtn: UIButton!
    @IBOutlet weak var infoBtn: UIBarButtonItem!
    @IBOutlet weak var hintBtn: UIBarButtonItem!
    @IBOutlet weak var newTestInfo: UITextView!
    @IBOutlet weak var healthSurveyInfo: UITextView!
    @IBOutlet weak var userSurveyInfo: UITextView!
    @IBOutlet weak var newTestInfoBtn: UIButton!
    @IBOutlet weak var healthSurveyInfoBtn: UIButton!
    @IBOutlet weak var userSurveyInfoBtn: UIButton!
    
    var toastLabel: UILabel!
    var state = 0
    var deviceID: String = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let launchedBefore = UserDefaults.standard.bool(forKey: "launchedBefore")
        if launchedBefore  {
            print("Not first launch.")
            deviceID = UserDefaults.standard.object(forKey: "deviceID") as! String
            print("device id: \(String(describing: deviceID))")
        } else {
            print("First launch, setting UserDefault.")
            UserDefaults.standard.set(true, forKey: "launchedBefore")
            deviceID = UUID().uuidString
            UserDefaults.standard.set(deviceID, forKey: "deviceID")
            print("device id: \(String(describing: deviceID))")

            // Terms of Service
            //let terms = ELAManager.termsOfServiceViewController()
            //self.navigationController?.pushViewController(terms!, animated: true)
        }
        
        UIPasteboard.general.string = deviceID

        if(tests.isEmpty) {
            UserDefaults.standard.set(0, forKey: "testID")
        }
        
        // setting a value for a key
        let userDefaults = UserDefaults.standard
        let encodedData: Data = NSKeyedArchiver.archivedData(withRootObject: tests)
        userDefaults.set(encodedData, forKey: "tests")
        userDefaults.synchronize()
        
        // retrieving a value for a key
        if let data = UserDefaults.standard.data(forKey: "tests"),
            let myTests = NSKeyedUnarchiver.unarchiveObject(with: data) as? [Test] {
            tests = myTests
        } else {
            print("There is an issue")
        }
        
        self.navigationItem.rightBarButtonItem = self.editButtonItem
        
        newTestInfoBtn.addTarget(self, action: #selector(TestsTableViewController.newTestInfoDown(_:)), for: .touchDown)
        newTestInfoBtn.addTarget(self, action: #selector(TestsTableViewController.newTestInfoUp(_:)), for: [.touchUpInside, .touchUpOutside])
        
        healthSurveyInfoBtn.addTarget(self, action: #selector(TestsTableViewController.healthInfoDown(_:)), for: .touchDown)
        healthSurveyInfoBtn.addTarget(self, action: #selector(TestsTableViewController.healthInfoUp(_:)), for: [.touchUpInside, .touchUpOutside])
        
        userSurveyInfoBtn.addTarget(self, action: #selector(TestsTableViewController.userInfoDown(_:)), for: .touchDown)
        userSurveyInfoBtn.addTarget(self, action: #selector(TestsTableViewController.userInfoUp(_:)), for: [.touchUpInside, .touchUpOutside])
    }
    
    func newTestInfoDown(_ sender: UIButton) {
        newTestInfo.isHidden = false
    }
  
    func newTestInfoUp(_ sender: UIButton) {
        newTestInfo.isHidden = true
    }
  
    func healthInfoDown(_ sender: UIButton) {
        healthSurveyInfo.isHidden = false
    }
    
    func healthInfoUp(_ sender: UIButton) {
        healthSurveyInfo.isHidden = true
    }
    
    func userInfoDown(_ sender: UIButton) {
        userSurveyInfo.isHidden = false
    }
    
    func userInfoUp(_ sender: UIButton) {
        userSurveyInfo.isHidden = true
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.tableView.reloadData()
        if(tests.isEmpty) {
            noTest.isHidden = false
        } else {
            noTest.isHidden = true
        }
        print("In viewWillAppear")
        // setting a value for a key
        let userDefaults = UserDefaults.standard
        let encodedData: Data = NSKeyedArchiver.archivedData(withRootObject: tests)
        userDefaults.set(encodedData, forKey: "tests")
        userDefaults.synchronize()
        
        // retrieving a value for a key
        if let data = UserDefaults.standard.data(forKey: "tests"),
            let myTests = NSKeyedUnarchiver.unarchiveObject(with: data) as? [Test] {
            tests = myTests
        } else {
            print("There is an issue")
        }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    @IBAction func didTapInfo(_ sender: UIBarButtonItem) {
        if(state == 1) {
            toastLabel.removeFromSuperview()
        }
        showToast(message: "COPYRIGHT:\nCopyright 2017 SMARTFORM.\nThe Ohio State University.\nAll Rights Reserved.\n\nSmartPhone App for Residential Testing of Formaldehyde (SmART-Form)\nPI: Karen Dannemiller\nCo-PI: Rongjun Qin\nDeveloper: Siyang Zhang.\n\nUnique device ID: \(String(describing: deviceID))\nYour ID has been automatically copied to the clipboard, please paste it along with your survey.\n\nThis app is currently under beta test, all measurements are not reliable and not related to your actual surroundings.")

    }
    
    @IBAction func didTapHint(_ sender: UIBarButtonItem) {
        if(state == 1) {    
            toastLabel.removeFromSuperview()
        }
        showToast(message: "Instructions:\n1.You should have an electronic or paper copy of images of badges “before” and “after” exposures. These are just images to simulate an actual test and there will be no color change during this beta test. These images are provided so that you can give us feedback on use of the app. \n2.Open the app and select “New test” \n3.Name your test and tap the black block to take a picture of the test location \n4.Select the “Before” button and take a picture of the “Before” image \n5.Select the “After” button and take a picture of the “After” image \n6.Select “Upload” and complete the sampling survey as much as you like \n7.Complete the follow-up beta testing survey, available on the main screen.")
    }
    
    @IBAction func didTapUpload(_ sender: UIButton) {
        UIApplication.shared.openURL(NSURL(string: "https://osu.az1.qualtrics.com/jfe/form/SV_eIPLorec5u70O3z")! as URL)
    }

    @IBAction func didTapUser(_ sender: UIButton) {
        UIApplication.shared.openURL(NSURL(string: "https://osu.az1.qualtrics.com/jfe/form/SV_1YsK1f5dZVByyxL")! as URL)
    }
    
    // Override to suppport editing the table view
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            tests.remove(at: indexPath.row)
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            
        }
    }
    
    // Override to support rearranging the table view
    override func tableView(_ tableView: UITableView, moveRowAt sourceIndexPath: IndexPath, to destinationIndexPath: IndexPath) {
        
    }
    
    // MARK: - UITableViewDataSource
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tests.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: identifier, for: indexPath) as! TestCell
        //guard let tests = tests else { return cell }
        
        let test = tests[indexPath.row] as Test
        
        cell.test = test
        
        return cell;
    }
    
    // MARK: - View Transfer
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showTest" {
            if let cell = sender as? UITableViewCell,
                let indexPath = tableView.indexPath(for: cell),
                let testVC = segue.destination as? TestViewController {
                testVC.test = tests[(indexPath as NSIndexPath).row]
            }
        }
    }
    
    
    @IBAction func saveTestDetail(_ segue:UIStoryboardSegue) {
        if let testVC = segue.source as? TestViewController {
            
            //add the new test to the tests array
            if let test = testVC.test {
                if tests.contains(where: {$0.id == test.id} ) {
                    let rowindex = tests.index(where: {$0.id == test.id} )
                    tests[rowindex!] = test
                    print("update old test")
                    //update the tableView
                    //let indexPath = IndexPath(row: rowindex!, section: 0)
                    //tableView.insertRows(at: [indexPath], with: .automatic)
                  
                }
                else {
                    tests.append(test)
                    print("add new test")
                    //update the tableView
                    let indexPath = IndexPath(row: tests.count-1, section: 0)
                    tableView.insertRows(at: [indexPath], with: .automatic)
                }
            }
        }
    }
    
    func showToast(message : String) {
        toastLabel = UILabel(frame: CGRect(x: 5, y: 5, width: 310, height: 445))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .left;
        toastLabel.font = UIFont(name: "Montserrat-Light", size: 9.0)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.layer.cornerRadius = 10;
        toastLabel.clipsToBounds  =  true
        toastLabel.numberOfLines = 25
        self.view.addSubview(toastLabel)
        
        UIView.animate(withDuration: 1.0, delay: 9.0, options: .curveEaseOut, animations: {
            self.toastLabel.alpha = 0.0
            self.state = 1
        }, completion: {(isCompleted) in
            self.toastLabel.removeFromSuperview()
            self.state = 0
        })
    }
}
