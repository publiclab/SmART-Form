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

class CameraViewController: UIViewController {
    
    @IBOutlet weak var navigationBar: UINavigationBar!
    @IBOutlet weak var imgOverlay: UIImageView!
    @IBOutlet weak var hintOverlay: UILabel!
    @IBOutlet weak var btnCapture: UIButton!
    @IBOutlet weak var btnCancel: UIButton!
    @IBOutlet weak var bottomOverlay: UILabel!
    
    let captureSession = AVCaptureSession()
    let stillImageOutput = AVCaptureStillImageOutput()
    var previewLayer: AVCaptureVideoPreviewLayer?
    var cropImage: UIImage?
    var ratio: String? = nil
    var newRequest: Int?
    
    // If we find a device we'll store it here for later use
    var captureDevice : AVCaptureDevice?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        captureSession.sessionPreset = AVCaptureSession.Preset.high
        
        if let device = AVCaptureDevice.default(for: AVMediaType.video), device.hasTorch {
            do {
                try device.lockForConfiguration()
                try device.setTorchModeOn(level: 1.0)
                device.torchMode = .off
                device.autoFocusRangeRestriction = AVCaptureDevice.AutoFocusRangeRestriction.near
                device.setWhiteBalanceModeLocked(with: device.deviceWhiteBalanceGains(for: AVCaptureDevice.WhiteBalanceTemperatureAndTintValues.init(temperature: 4000, tint: 0)), completionHandler: { (time) in })
                device.setExposureModeCustom(duration: CMTimeMake(1,125), iso: 200, completionHandler: { (time) in })
                device.setExposureTargetBias(0, completionHandler: { (time) in })
                device.unlockForConfiguration()
                // Finally check the position and confirm we've got the back camera
                if(device.position == AVCaptureDevice.Position.back) {
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
        if let device = AVCaptureDevice.default(for: AVMediaType.video), device.hasTorch {
            do {
                try device.lockForConfiguration()
                //let torchOn = !device.isTorchActive
                //try device.setTorchModeOnWithLevel(1.0)
                device.torchMode = .off
                device.unlockForConfiguration()
            } catch {
                print("error")
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "toImageView" {
            let dvc = segue.destination as! ImageViewController
            dvc.newImage = self.cropImage
            dvc.newRequest = self.newRequest
        }
        
    }
    
    @IBAction func captureImage(_ sender: Any) {
        
        if let videoConnection = stillImageOutput.connection(with: AVMediaType.video) {
            
            stillImageOutput.captureStillImageAsynchronously(from: videoConnection, completionHandler: { (CMSampleBuffer, Error) in
                if let imageData = AVCaptureStillImageOutput.jpegStillImageNSDataRepresentation(CMSampleBuffer!) {
                    
                    if let image = UIImage(data: imageData) {
                        self.cropImage = image.cropToBounds(image: image, width: 500.0, height: 500.0)
                        
                        //UIImageWriteToSavedPhotosAlbum(cropImage, nil, nil, nil)
                    }
                }
            })
        }
        
        let when = DispatchTime.now() + 1 // change 2 to desired number of seconds
        DispatchQueue.main.asyncAfter(deadline: when) {
            self.performSegue(withIdentifier: "toImageView", sender: self)
        }
    }
    
    func beginSession() {
        
        do {
            try captureSession.addInput(AVCaptureDeviceInput(device: captureDevice!))
            stillImageOutput.outputSettings = [AVVideoCodecKey:AVVideoCodecJPEG]
            
            if captureSession.canAddOutput(stillImageOutput) {
                captureSession.addOutput(stillImageOutput)
            }
            
        }
        catch {
            print("error: \(error.localizedDescription)")
        }
        
        let previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        
        self.view.layer.addSublayer(previewLayer)
        previewLayer.frame = self.view.layer.frame
        captureSession.startRunning()
        
        self.view.addSubview(hintOverlay)
        self.view.addSubview(imgOverlay)
        self.view.addSubview(btnCapture)
        self.view.addSubview(btnCancel)
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


