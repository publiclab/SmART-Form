//
//  TestViewController.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/4/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//


import UIKit

class TestViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate{

    @IBOutlet weak var temperaturePicker: UIPickerView!
    @IBOutlet weak var humidityPicker: UIPickerView!
    @IBOutlet var testTitle: UITextField!
    @IBOutlet var testResult: UITextField!
    @IBOutlet var imageView: UIImageView!
    @IBOutlet var beforeBtn: UIButton!
    @IBOutlet var afterBtn: UIButton!
    @IBOutlet var uploadBtn: UIButton!
    @IBOutlet var aboutBtn: UIButton!

    var test: Test?
    var testID: String?
    var temperatureData = ["<65F", "65-80F", ">80F"]
    var humidityData = ["0-80%", ">80%"]
    var temperature: String?
    var humidity: String?
    var toastLabel: UILabel!
    var state = 0
    
    required init?(coder aDecoder: NSCoder) {
        print("init TestViewController")
        super.init(coder: aDecoder)
    }
    
    deinit {
        print("deinit TestViewController")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.imageView.image = #imageLiteral(resourceName: "icon-camera-hint")
        self.testTitle.becomeFirstResponder()
        self.afterBtn.isEnabled = false
        self.afterBtn.backgroundColor = UIColor.gray
        // create tap gesture recognizer
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(TestViewController.imageTapped(gesture:)))
        let longGesture = UILongPressGestureRecognizer(target: self, action: #selector(TestViewController.imageLongPressed(gesture:)))

        // add it to the image view;
        imageView.addGestureRecognizer(tapGesture)
        imageView.addGestureRecognizer(longGesture)
        
        // make sure imageView can be interacted with by user
        imageView.isUserInteractionEnabled = true
        
        //Looks for single or multiple taps.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(TestViewController.dismissKeyboard))
        
        //Uncomment the line below if you want the tap not not interfere and cancel other interactions.
        tap.cancelsTouchesInView = false
        
        view.addGestureRecognizer(tap)
        
        let storedID = UserDefaults.standard.string(forKey: "testID")
        
        testID = test?.id
        if (testID == nil) {
            testID = String(Int(storedID!)! + 1)
        }
 
        print("testID: \(String(describing: testID))")
        print("id: \(String(describing: test?.id))")
        
        UserDefaults.standard.set(testID, forKey: "testID")

        testTitle.text = test?.title
        testResult.text = test?.result
        if (test?.image != nil) {
            imageView.image = test?.image
        }
        temperature = test?.temperature
        humidity = test?.humidity
        temperaturePicker.delegate = self
        humidityPicker.delegate = self
        
        if let temperature = temperature, !temperature.isEmpty {
            var defaultRowIndex1 = temperatureData.index(of: temperature)
            if(defaultRowIndex1 == nil) { defaultRowIndex1 = 0 }
            temperaturePicker.selectRow(defaultRowIndex1!, inComponent: 0, animated: false)
        }
        if let humidity = humidity, !humidity.isEmpty {
            var defaultRowIndex2 = humidityData.index(of: humidity)
            if(defaultRowIndex2 == nil) { defaultRowIndex2 = 0 }
            humidityPicker.selectRow(defaultRowIndex2!, inComponent: 0, animated: false)
        }
      
        testResult.isUserInteractionEnabled = false

        let launchedBefore = UserDefaults.standard.bool(forKey: "launchedBefore")
        if launchedBefore  {
            print("Not first launch.")
        } else {
            print("First launch, setting UserDefault.")
            UserDefaults.standard.set(true, forKey: "launchedBefore")
            showToast(message: "User ID copied to clipboard!")
        }

    }
    
    //Calls this function when the tap is recognized.
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    
    func imageTapped(gesture: UIGestureRecognizer) {
        // if the tapped view is a UIImageView then set it to imageview
        if (gesture.view as? UIImageView) != nil {
            //Here you can initiate your new ViewController
            let imagePicker = UIImagePickerController()
            
            if UIImagePickerController.isSourceTypeAvailable(.camera) {
                imagePicker.sourceType = .camera
            } else {
                imagePicker.sourceType = .photoLibrary
            }
            
            imagePicker.delegate = self
            
            present(imagePicker, animated: true, completion: nil)
        }
    }
    
    func imageLongPressed(gesture: UIGestureRecognizer) {
        // if the tapped view is a UIImageView then set it to imageview
        if (gesture.view as? UIImageView) != nil {
            //showToast(message: "Tap here to snap the test location!")
        }
    }
    
    @IBAction func didTapHint(_ sender: UIBarButtonItem) {
        if(state == 1) {
            toastLabel.removeFromSuperview()
        }
        showToast(message: "Instructions:\n1.You should have an electronic or paper copy of images of badges “before” and “after” exposures. These are just images to simulate an actual test and there will be no color change during this beta test. These images are provided so that you can give us feedback on use of the app. \n2.Open the app and select “New test” \n3.Name your test and tap the black block to take a picture of the test location \n4.Select the “Before” button and take a picture of the “Before” image \n5.Select the “After” button and take a picture of the “After” image \n6.Select “Upload” and complete the sampling survey as much as you like \n7.Complete the follow-up beta testing survey, available on the main screen.")
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int{
        if pickerView == temperaturePicker {
            return temperatureData.count
        } else if pickerView == humidityPicker{
            return humidityData.count
        }
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if pickerView == temperaturePicker {
            return temperatureData[row]
        } else if pickerView == humidityPicker{
            return humidityData[row]
        }
        return ""
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent compnnent: Int) {
        if pickerView == temperaturePicker {
            temperature = temperatureData[row]
        } else if pickerView == humidityPicker{
            humidity = humidityData[row]
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "SaveTestDetail" {
            test = Test(id: testID, title: testTitle.text, result: testResult.text, date: Date(), image: imageView.image, temperature: temperature, humidity: humidity)
        }
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    @IBAction func captureBadge(_ segue:UIStoryboardSegue) {
    }
    
    @IBAction func beforeImg(_ sender: UIButton) {
        beforeBtn.backgroundColor = UIColor.cyan
        afterBtn.isEnabled = true
        afterBtn.backgroundColor = UIColor(red:0.00, green:0.48, blue:1.00, alpha:1.0)
    }
    
    @IBAction func afterImg(_ sender: UIButton) {
        afterBtn.backgroundColor = UIColor.cyan
        let when = DispatchTime.now() + 2 // change 2 to desired number of seconds
        DispatchQueue.main.asyncAfter(deadline: when) {
            // For beta test only, need data model to update
            let rppb = arc4random_uniform(10);
            self.testResult.text = String(rppb) + " ppb"
            self.testResult.backgroundColor = UIColor.cyan
        }
    }
    
    @IBAction func uploadData(_ sender: UIButton) {
        UIApplication.shared.openURL(NSURL(string: "https://osu.az1.qualtrics.com/jfe/form/SV_5u9FnmAiYtRQtp3")! as URL)
        uploadBtn.backgroundColor = UIColor.cyan
    }
    
    @IBAction func aboutForm(_ sender: UIButton) {
        aboutBtn.backgroundColor = UIColor.cyan
    }
    

    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        
        // Get picked image from info dictionary
        let image = info[UIImagePickerControllerOriginalImage] as! UIImage
        
        // Put that image on the screen in the image view
        imageView.image = image
        
        // Take image picker off the screen -
        // you must call this dismiss method
        dismiss(animated: true, completion: nil)
    }

    func showToast(message : String) {
        toastLabel = UILabel(frame: CGRect(x: 5, y: 70, width: 310, height: 445))
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
