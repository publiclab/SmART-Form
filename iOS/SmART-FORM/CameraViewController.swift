//
//  CameraViewController.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 6/5/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//
import UIKit
import AVFoundation
import Foundation

protocol ViewControllerBDelegate: class {
    
    func ratioChanged(ratio:String?)
    
}

class CameraViewController: UIViewController {
    weak var delegate: ViewControllerBDelegate?

    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var imgOverlay: UIImageView!
    @IBOutlet weak var hintOverlay: UILabel!
    @IBOutlet weak var btnCapture: UIButton!
    @IBOutlet weak var bottomOverlay: UILabel!

    let captureSession = AVCaptureSession()
    let stillImageOutput = AVCaptureStillImageOutput()
    var previewLayer : AVCaptureVideoPreviewLayer?
    var ratio : String? = nil
    var name : String? = nil
    
    // If we find a device we'll store it here for later use
    var captureDevice : AVCaptureDevice?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        captureSession.sessionPreset = AVCaptureSessionPresetHigh
        
        if let device = AVCaptureDevice.defaultDevice(withMediaType: AVMediaTypeVideo), device.hasTorch {
            do {
                try device.lockForConfiguration()
                try device.setTorchModeOnWithLevel(1.0)
                device.torchMode = .off
                device.autoFocusRangeRestriction = AVCaptureAutoFocusRangeRestriction.near
                device.setWhiteBalanceModeLockedWithDeviceWhiteBalanceGains(device.deviceWhiteBalanceGains(for: AVCaptureWhiteBalanceTemperatureAndTintValues.init(temperature: 4000, tint: 0)), completionHandler: { (time) in })
                device.setExposureModeCustomWithDuration(CMTimeMake(1, 30), iso: 200, completionHandler: { (time) in })
                device.setExposureTargetBias(0, completionHandler: { (time) in })
               
                device.unlockForConfiguration()
                // Finally check the position and confirm we've got the back camera
                if(device.position == AVCaptureDevicePosition.back) {
                    captureDevice = device
                    if captureDevice != nil {
                        print("Capture device found")
                        beginSession()
                    }
                }
            } catch {
                print("error")
            }
        }
    }
    
    func toggleFlash() {
        if let device = AVCaptureDevice.defaultDevice(withMediaType: AVMediaTypeVideo), device.hasTorch {
            do {
                try device.lockForConfiguration()
                let torchOn = !device.isTorchActive
                try device.setTorchModeOnWithLevel(1.0)
                device.torchMode = torchOn ? .on : .off
                device.unlockForConfiguration()
            } catch {
                print("error")
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        /*
        if segue.identifier == "toViewPage" {
            let dvc = segue.destination as! ImageViewController
            dvc.newImage = cropImage
        } */
    }
    
    
    func saveToCamera() {
        
        if let videoConnection = stillImageOutput.connection(withMediaType: AVMediaTypeVideo) {
            
            stillImageOutput.captureStillImageAsynchronously(from: videoConnection, completionHandler: { (CMSampleBuffer, Error) in
                if let imageData = AVCaptureStillImageOutput.jpegStillImageNSDataRepresentation(CMSampleBuffer) {
                    
                    if let image = UIImage(data: imageData) {
                        let cropImage = image.cropToBounds(image: image, width: 500.0, height: 500.0)
                        
                        self.ratio = String(format:"%.5f", cropImage.getRatio(image: cropImage))
                        self.delegate?.ratioChanged(ratio: self.ratio)
                        print("saveToCamera", self.ratio!)
                        CustomPhotoAlbum.sharedInstance.save(image: cropImage)
                        //UIImageWriteToSavedPhotosAlbum(cropImage, nil, nil, nil)
                    }
                }
            })
        }
    }
    
    @IBAction func actionCameraCapture(_ sender: UIStoryboardSegue) {
        saveToCamera()
        print("Camera button pressed")
    }
    
    func beginSession() {
        
        do {
            try captureSession.addInput(AVCaptureDeviceInput(device: captureDevice))
            stillImageOutput.outputSettings = [AVVideoCodecKey:AVVideoCodecJPEG]
            
            if captureSession.canAddOutput(stillImageOutput) {
                captureSession.addOutput(stillImageOutput)
            }
            
        }
        catch {
            print("error: \(error.localizedDescription)")
        }
        
        guard let previewLayer = AVCaptureVideoPreviewLayer(session: captureSession) else {
            print("no preview layer")
            return
        }
        
        self.view.layer.addSublayer(previewLayer)
        previewLayer.frame = self.view.layer.frame
        captureSession.startRunning()
        
        self.view.addSubview(hintOverlay)
        self.view.addSubview(imgOverlay)
        self.view.addSubview(btnCapture)
        self.view.addSubview(bottomOverlay)
    }

    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        let screenSize = view.bounds.size
        if let touchPoint = touches.first {
            let x = touchPoint.location(in: view).y / screenSize.height
            let y = 1.0 - touchPoint.location(in: view).x / screenSize.width
            let focusPoint = CGPoint(x: x, y: y)
            
            if let device = captureDevice {
                do {
                    try device.lockForConfiguration()
                    
                    device.focusPointOfInterest = focusPoint
                    //device.focusMode = .continuousAutoFocus
                    device.focusMode = .autoFocus
                    //device.focusMode = .locked
                    device.exposurePointOfInterest = focusPoint
                    //device.exposureMode = AVCaptureExposureMode.continuousAutoExposure
                    device.unlockForConfiguration()
                }
                catch {
                    // just ignore
                }
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
    
    func getLightness(image: UIImage) -> Double {
        let width = Int(image.size.width)
        let height = Int(image.size.height)
        var sum = 0.0
        var pixelCounter = 0
        for j in 0 ..< height{
            for i in 0 ..< width{
                pixelCounter += 1
                sum += image.getPixelIntensity(pos: CGPoint(x: CGFloat(i), y: CGFloat(j)))
            }
        }
        print("lightness: ", sum/Double(pixelCounter))
        return sum/Double(pixelCounter)
    }
    
    func getRatio(image: UIImage) -> Double {
        var r = 0.0
        let contextImage: UIImage = UIImage(cgImage: image.cgImage!)

        let rect1: CGRect = CGRect(x: 300.0, y: 100.0, width: 100.0, height: 100.0)
        let rect2: CGRect = CGRect(x: 300.0, y: 300.0, width: 100.0, height: 100.0)

        // Create bitmap image from context using the rect
        let imageR: CGImage = contextImage.cgImage!.cropping(to: rect1)!
        let imageRef: UIImage = UIImage(cgImage: imageR)
        let imageA: CGImage = contextImage.cgImage!.cropping(to: rect2)!
        let imageAct: UIImage = UIImage(cgImage: imageA)

        r = getLightness(image: imageAct) / getLightness(image: imageRef)
        
        print("ratio", r)
        return r
    }
}
