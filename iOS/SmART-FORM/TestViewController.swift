//
//  TestViewController.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/4/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//


import UIKit

class TestViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate{
    
    @IBOutlet var currentTimeLabel: UILabel!
    @IBOutlet var temperaturePicker: UIPickerView!
    @IBOutlet var humidityPicker: UIPickerView!
    @IBOutlet var testTitle: UITextField!
    @IBOutlet var beforeBtn: UIButton!
    @IBOutlet var afterBtn: UIButton!
    @IBOutlet var testResult: UILabel!
    @IBOutlet var unitType: UILabel!
    @IBOutlet var uploadBtn: UIButton!
    
    var test: Test?
    var testID: String?
    var testDate: Date?
    var testStart: Date?
    var testEnd: Date?
    var testBefore: UIImage?
    var testAfter: UIImage?
    var temperatureData = ["<65°F", "65-80°F", ">80°F"]
    var humidityData = ["0-80% RH", ">80% RH"]
    var temperature: String?
    var humidity: String?
    var toastLabel: UILabel!
    var crtState = 0
    var hintState = 0
    var dataReceived: Int?
    var imageReceived: UIImage?
    var timerTest : Timer?

    required init?(coder aDecoder: NSCoder) {
        print("init TestViewController")
        super.init(coder: aDecoder)
    }
    
    deinit {
        print("deinit TestViewController")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.afterBtn.isEnabled = false
    
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

        if test?.state != nil {
            crtState = (test?.state)!
        } else {
            crtState = 0
        }
        
        testTitle.text = test?.title
        testResult.text = test?.result
        testDate = test?.date
        if testDate != nil {
            currentTimeLabel.text = testDate?.toString(dateFormat: "MMM dd, yyyy HH:mm")
        } else {
            testDate = Date()
            currentTimeLabel.text = testDate?.toString(dateFormat: "MMM dd, yyyy HH:mm")
        }
        testStart = test?.timeStart
        testEnd = test?.timeEnd
        testBefore = test?.before
        if testBefore != nil {
            self.beforeBtn.setBackgroundImage(testBefore, for: .normal)
        }
        testAfter = test?.after
        if testAfter != nil {
            self.afterBtn.setBackgroundImage(testAfter, for: .normal)
        }
        
        if crtState == 0 {
            unitType.text = ""
        } else if crtState == 1 {
            startTimer()
            unitType.text = "remaining"
            self.afterBtn.isEnabled = true
        } else if crtState == 2 {
            unitType.text = "ppb"
            self.afterBtn.isEnabled = true
        }

        temperature = test?.temperature
        humidity = test?.humidity
        temperaturePicker.delegate = self
        humidityPicker.delegate = self
        
        let defaultRow1 = 1
        temperaturePicker.selectRow(defaultRow1, inComponent: 0, animated: false)
        
        if let temperature = temperature, !temperature.isEmpty {
            var defaultRowIndex1 = temperatureData.index(of: temperature)
            if(defaultRowIndex1 == nil) { defaultRowIndex1 = 0 }
            temperaturePicker.selectRow(defaultRowIndex1!, inComponent: 0, animated: false)
        }
        if let humidity = humidity, !humidity.isEmpty {
            var defaultRowIndex2 = humidityData.index(of: humidity)
            if(defaultRowIndex2 == nil) { defaultRowIndex2 = 0 }
            // If humidity>80%, popup warning
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
    @objc func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
   
    
    @IBAction func didTapHint(_ sender: UIBarButtonItem) {
        if(hintState == 1) {
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
            if(row == 1){
                let alert = UIAlertController(title: "Is the relative humidity high?", message: "The badge is unstable under high humidity (>80%). It's recommended you retake the photo with lower humidity for a better result.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "Got it!", style: .default, handler: nil))
                //alert.addAction(UIAlertAction(title: "No", style: .cancel, handler: nil))
                self.present(alert, animated: true)
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "SaveTestDetail" {
            test = Test(id: testID!, title: testTitle.text, result: testResult.text, date: testDate, timeStart: testStart, timeEnd: testEnd, before: testBefore, after: testAfter, temperature: temperature, humidity: humidity, state: crtState)
        }
        else if segue.identifier == "takeBefore" {
            let destinationVC = segue.destination as! CameraViewController
            destinationVC.newRequest = 0
        }
        else if segue.identifier == "takeAfter" {
            let destinationVC = segue.destination as! CameraViewController
            destinationVC.newRequest = 1
        }
    }
    
    func ratioChanged(ratio: String?) {
        testResult.text = ratio
    }
    
    // segue ViewControllerB -> ViewController
    @IBAction func unwindToThisView(sender: UIStoryboardSegue) {
        if let sourceViewController = sender.source as? ImageViewController {
            imageReceived = sourceViewController.newImage//.resizeImage(image: sourceViewController.newImage, targetSize: CGSize(width: 250, height: 250))
            dataReceived = sourceViewController.newRequest
            print("unwind to test")
            if dataReceived == 0 {
                print("set before")
                self.beforeBtn.setBackgroundImage(imageReceived, for: .normal)
                testBefore = imageReceived
                handleBefore()
            }
            else if dataReceived == 1 {
                print("set after")
                self.afterBtn.setBackgroundImage(imageReceived, for: .normal)
                testAfter = imageReceived
                handleAfter()
            }
        }
    }
    

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    func startTimer() {
        testEnd = testStart?.addingTimeInterval(TimeInterval(3*24*3600))
        setTimeLeft()
        timerTest = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(self.setTimeLeft), userInfo: nil, repeats: true)
    }
    
    func stopTimer() {
        timerTest?.invalidate()
    }
    
    @objc func setTimeLeft() {
        let timeNow = Date()
        if let timeEnd = testEnd {
            // Only keep counting if timeEnd is bigger than timeNow
            if timeEnd.compare(timeNow) == ComparisonResult.orderedDescending {
                let calendar = Calendar.current
                let components = calendar.dateComponents([.day , .hour , .minute , .second], from: timeNow, to: timeEnd)
                
                var dayText = String(describing: components.day!) + "d "
                var hourText = String(describing: components.hour!) + "h "
                
                // Hide day and hour if they are zero
                if let d = components.day {
                    if d <= 0 {
                        dayText = ""
                        if let h = components.hour {
                            if h <= 0 {
                                hourText = ""
                            }
                        }
                    }
                }
                
                testResult.text = dayText + hourText + String(describing: components.minute!) + "m " + String(describing: components.second!) + "s"
                
            } else {
                testResult.text = "Time Up!"
            }
        }
    }
    
    
    func handleBefore() {
        self.crtState = 1

        self.afterBtn.isEnabled = true
        

        let when = DispatchTime.now() + 1 // change 2 to desired number of seconds
        DispatchQueue.main.asyncAfter(deadline: when) {
            self.testStart = Date()
            self.startTimer()
            
            if let selectedDate = self.testEnd {
                print("add notification")
                print(selectedDate)
                let delegate = UIApplication.shared.delegate as? AppDelegate
                delegate?.scheduleNotification(at: selectedDate)
            }
            self.unitType.text = "remaining"
            
            let ratio = self.testBefore?.getRatio(image: self.testBefore!)
            
            if let ratioW = ratio {
                if(ratioW > 1.0) {
                    let alert = UIAlertController(title: "Is your badge contaminated?", message: "Check you badge to see if it is already exposed. If so, use another badge.", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Got it!", style: .default, handler: nil))
                    self.present(alert, animated: true)
                }
            }
            
            let alert = UIAlertController(title: "Waiting for the next step?", message: "You can take the health survey now.", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "Got it!", style: .default, handler: nil))
            self.present(alert, animated: true)
        }
    }
    
    func handleAfter() {
        self.crtState = 2
        self.stopTimer()
        let timeNow = Date()
        let interval = timeNow.timeIntervalSince(testStart!)
        print("second interval", interval)
        let hour = Double(NSInteger(interval))/3600.0
        print("hour interval", hour)
        
        let when = DispatchTime.now() + 1 // change 2 to desired number of seconds
        DispatchQueue.main.asyncAfter(deadline: when) {

            /**
             * Linear regression model
             * (final result in ppb)= [-42402*(ratio from image)+ 42621]/(hours)
             */
            
            let ratio = self.testBefore?.getRatio(image: self.testAfter!) ?? 42621/42402
            let rppb = (-42402*ratio + 42621)/hour

            if(rppb>999) {
                self.testResult.text = String(999)
            } else if(rppb<0) {
                self.testResult.text = String(0)
            } else {
                self.testResult.text = String(NSInteger(rppb))
            }
            self.unitType.text = "ppb"
            
            // If badge bluish detected, ask user to retake experiment
            let bluish = self.testAfter?.getBluish(image: self.testAfter!) ?? false
            if(bluish) {
                let alert = UIAlertController(title: "Is your badge bluish?", message: "Relative humidity above 80% can cause incorrect results. Use a new badge in an area of low relative humidity.", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "Got it!", style: .default, handler: nil))
                self.present(alert, animated: true)
            }
            
            // If result < 20ppb, ask user to retake after image
            if(hour < 24) {
                let alert = UIAlertController(title: "Your exposure time is below the detection limit", message: "Your badge exposure time is less than 24 hours. For a more accurate result you can optionally expose the badge for another two days and retake the photo.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "Got it!", style: .default, handler: nil))
                self.present(alert, animated: true)
                
            } else if(rppb*hour < 1440) {
                let alert = UIAlertController(title: "Your result is below the detection limit", message: "Your formaldehyde concentration is low (<20ppb). For a more accurate result you can optionally expose the badge for another four days and retake the photo.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "Got it!", style: .default, handler: nil))
                self.present(alert, animated: true)
                
            } else if (rppb*hour > 6480) {
                let alert = UIAlertController(title: "Your result is above the detection limit", message: "Your formaldehyde concentration is elevated and has saturated the badge. You can retest with a new badge and take an image after 24 hours for more accurate results.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "Got it!", style: .default, handler: nil))
                self.present(alert, animated: true)
            }
        }
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
            self.hintState = 1
        }, completion: {(isCompleted) in
            self.toastLabel.removeFromSuperview()
            self.hintState = 0
        })
    }

}
extension Date
{
    func toString( dateFormat format  : String ) -> String
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(from: self)
    }
}
extension String
{
    func toDate( dateFormat format  : String) -> Date
    {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        dateFormatter.timeZone = NSTimeZone(name: "UTC") as TimeZone!
        return dateFormatter.date(from: self)!
    }
}

extension UIImage {
    func cropToBounds(image: UIImage, width: Double, height: Double) -> UIImage {
        let contextImage: UIImage = UIImage(cgImage: image.cgImage!)
        let contextSize: CGSize = contextImage.size
        let posX: CGFloat = contextSize.width
        let posY: CGFloat = contextSize.height
        let cgwidth: CGFloat = CGFloat(width)
        let cgheight: CGFloat = CGFloat(height)
        // See what size is longer and create the center off of that
        
        
        let rect: CGRect = CGRect(x: posX/2-cgwidth/2, y: posY/2-cgheight/2, width: cgwidth, height: cgheight)
        // Create bitmap image from context using the rect
        let imageRef: CGImage = contextImage.cgImage!.cropping(to: rect)!
        
        // Create a new image based on the imageRef and rotate back to the original orientation
        let image = UIImage(cgImage: imageRef, scale: cgwidth, orientation: image.imageOrientation)
        return image
    }
    
    func resizeImage(image: UIImage, targetSize: CGSize) -> UIImage {
        let size = image.size
        
        let widthRatio  = targetSize.width  / size.width
        let heightRatio = targetSize.height / size.height
        
        // Figure out what our orientation is, and use that to form the rectangle
        var newSize: CGSize
        if(widthRatio > heightRatio) {
            newSize = CGSize(width: size.width * heightRatio, height: size.height * heightRatio)
        } else {
            newSize = CGSize(width: size.width * widthRatio,  height: size.height * widthRatio)
        }
        
        // This is the rect that we've calculated out and this is what is actually used below
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
        
        // Actually do the resizing to the rect using the ImageContext stuff
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        image.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return newImage!
    }
    
    func getPixelIntensity(pos: CGPoint) -> Double {
        
        let pixelData = self.cgImage!.dataProvider!.data
        let data: UnsafePointer<UInt8> = CFDataGetBytePtr(pixelData)
        
        let pixelInfo: Int = ((Int(self.size.width) * Int(pos.y)) + Int(pos.x)) * 4
        
        let r = Double(CGFloat(data[pixelInfo]) / CGFloat(255.0))
        let g = Double(CGFloat(data[pixelInfo+1]) / CGFloat(255.0))
        let b = Double(CGFloat(data[pixelInfo+2]) / CGFloat(255.0))
        //let a = Double(CGFloat(data[pixelInfo+3]) / CGFloat(255.0))
        
        return (r+g+b)/3.0
    }
    
    func getPixelSaturation(pos: CGPoint) -> Double {
        let pixelData = self.cgImage!.dataProvider!.data
        let data: UnsafePointer<UInt8> = CFDataGetBytePtr(pixelData)
        
        let pixelInfo: Int = ((Int(self.size.width) * Int(pos.y)) + Int(pos.x)) * 4
        
        let r = Double(CGFloat(data[pixelInfo]) / CGFloat(255.0))
        let g = Double(CGFloat(data[pixelInfo+1]) / CGFloat(255.0))
        let b = Double(CGFloat(data[pixelInfo+2]) / CGFloat(255.0))
        //let a = Double(CGFloat(data[pixelInfo+3]) / CGFloat(255.0))
        
        let s = 1 - 3.0/(r+g+b)*min(r,g,b) //Saturation
        
        return s
    }
    
    func getLightness(image: UIImage) -> Double {
        let width = Int(image.size.width)
        let height = Int(image.size.height)
        var sum = 0.0
        var pixelCounter = 0
        for j in stride(from: 0, through: height-1, by: 2){
            for i in stride(from: 0, through: width-1, by: 2){
                pixelCounter += 1
                sum += image.getPixelIntensity(pos: CGPoint(x: CGFloat(i), y: CGFloat(j)))
            }
        }
        print("lightness: ", sum/Double(pixelCounter))
        return sum/Double(pixelCounter)
    }
    
    func getSaturation(image: UIImage) -> Double {
        let width = Int(image.size.width)
        let height = Int(image.size.height)
        var sum = 0.0
        var pixelCounter = 0
        for j in stride(from: 0, through: height-1, by: 2){
            for i in stride(from: 0, through: width-1, by: 2){
                pixelCounter += 1
                sum += image.getPixelSaturation(pos: CGPoint(x: CGFloat(i), y: CGFloat(j)))
            }
        }
        print("saturation: ", sum/Double(pixelCounter))
        return sum/Double(pixelCounter)
    }
    
    func getRatio(image: UIImage) -> Double {
        var r = 0.0
        let contextImage: UIImage = UIImage(cgImage: image.cgImage!)
        
        let rect1: CGRect = CGRect(x: 300.0, y: 300.0, width: 100.0, height: 100.0)
        let rect2: CGRect = CGRect(x: 300.0, y: 100.0, width: 100.0, height: 100.0)
        
        // Create bitmap image from context using the rect
        let imageA: CGImage = contextImage.cgImage!.cropping(to: rect1)!
        let imageAct: UIImage = UIImage(cgImage: imageA)
        
        
        let imageR: CGImage = contextImage.cgImage!.cropping(to: rect2)!
        let imageRef: UIImage = UIImage(cgImage: imageR)
   
        r = getLightness(image: imageAct) / getLightness(image: imageRef)
        
        print("ratio", r)
        return r
    }
    
    
    func getBluish(image: UIImage) -> Bool {
        var s = 0.0
        let contextImage: UIImage = UIImage(cgImage: image.cgImage!)
        
        let rect1: CGRect = CGRect(x: 300.0, y: 300.0, width: 100.0, height: 100.0)
        let rect2: CGRect = CGRect(x: 300.0, y: 100.0, width: 100.0, height: 100.0)
        
        // Create bitmap image from context using the rect
        let imageA: CGImage = contextImage.cgImage!.cropping(to: rect1)!
        let imageAct: UIImage = UIImage(cgImage: imageA)
        
        let imageR: CGImage = contextImage.cgImage!.cropping(to: rect2)!
        let imageRef: UIImage = UIImage(cgImage: imageR)
        
        s = (getSaturation(image: imageAct) + getSaturation(image: imageRef))/2.0
                
        if(s<0.4){
            return true
        } else {
            return false
        }
        
    }
    
}
