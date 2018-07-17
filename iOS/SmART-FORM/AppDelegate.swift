//
//  AppDelegate.swift
//  SmART-FORM
//
//  Created by Siyang Zhang on 5/23/17.
//  Copyright © 2017 Siyang Zhang. All rights reserved.
//

import Onboard
import UIKit
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?

    func scheduleNotification(at date: Date) {
        let calendar = Calendar(identifier: .gregorian)
        let components = calendar.dateComponents(in: .current, from: date)
        let newComponents = DateComponents(calendar: calendar, timeZone: .current, month: components.month, day: components.day, hour: components.hour, minute: components.minute)
        
        let trigger = UNCalendarNotificationTrigger(dateMatching: newComponents, repeats: false)
        
        let content = UNMutableNotificationContent()
        content.title = "Badge Test Reminder"
        content.body = "It's time to take your badge image by 72 hours exposure!"
        content.sound = UNNotificationSound.default()
        
        let request = UNNotificationRequest(identifier: "textNotification", content: content, trigger: trigger)
        
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
        UNUserNotificationCenter.current().add(request) {(error) in
            if let error = error {
                print("Uh oh! We had an error: \(error)")
            }
        }
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler(.alert)
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) {(accepted, error) in
            if !accepted {
                print("Notification access denied.")
            }
        }
        
        let defaults = UserDefaults.standard
        
        let launchedBefore = defaults.bool(forKey: "launchedBefore")
        if launchedBefore  {
            print("Not first launch.")
            self.setupNormalRootViewController()
        } else {
            print("First launch, setting UserDefault.")
            
            let appDelegate = UIApplication.shared.delegate as! AppDelegate
            appDelegate.window = UIWindow(frame: UIScreen.main.bounds)
            appDelegate.window?.rootViewController = self.generateStandardOnboardingVC()
            appDelegate.window?.makeKeyAndVisible()
        }
        
        return true
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    func generateStandardOnboardingVC () -> OnboardingViewController {
        
        // Initialize onboarding view controller
        var onboardingVC = OnboardingViewController()

        // Create slides
        let firstPage = OnboardingContentViewController.content(withTitle: "Start new tests here", body: "Access multiple tests below", image: UIImage(named: "app_intro1"), buttonText: nil, action: nil)
        firstPage.titleLabel.font = UIFont.systemFont(ofSize: 20.0)
        firstPage.bodyLabel.font = UIFont.systemFont(ofSize: 18.0)

        let secondPage = OnboardingContentViewController.content(withTitle: "Peel off sticker", body: "Make sure the badge is exposed", image: UIImage(named: "app_intro2"), buttonText: nil, action: nil)
        secondPage.titleLabel.font = UIFont.systemFont(ofSize: 20.0)
        secondPage.bodyLabel.font = UIFont.systemFont(ofSize: 18.0)

        let thirdPage = OnboardingContentViewController.content(withTitle: "Take 'before' picture", body: "Then wait 72 hours", image: UIImage(named: "app_intro3"), buttonText: nil, action: nil)
        thirdPage.titleLabel.font = UIFont.systemFont(ofSize: 20.0)
        thirdPage.bodyLabel.font = UIFont.systemFont(ofSize: 18.0)

        let fourthPage = OnboardingContentViewController.content(withTitle: "After 72 hours, take 'after' picture", body: "Retake pictures if see warnings", image: UIImage(named: "app_intro4"), buttonText: nil, action: self.handleOnboardingCompletion)
        fourthPage.titleLabel.font = UIFont.systemFont(ofSize: 20.0)
        fourthPage.bodyLabel.font = UIFont.systemFont(ofSize: 18.0)

        let fifthPage = OnboardingContentViewController.content(withTitle: "Contribute your data", body: "Take the surveys and help us to improve the app", image: UIImage(named: "app_intro5"), buttonText: nil, action: self.handleOnboardingCompletion)
        fifthPage.titleLabel.font = UIFont.systemFont(ofSize: 20.0)
        fifthPage.bodyLabel.font = UIFont.systemFont(ofSize: 18.0)

        // Define onboarding view controller properties
        onboardingVC = OnboardingViewController.onboard(withBackgroundImage: UIImage(named: "purple"), contents: [firstPage, secondPage, thirdPage, fourthPage, fifthPage])
        onboardingVC.shouldFadeTransitions = true
        onboardingVC.shouldMaskBackground = false
        onboardingVC.shouldBlurBackground = false
        onboardingVC.fadePageControlOnLastPage = true
        onboardingVC.pageControl.pageIndicatorTintColor = UIColor.darkGray
        onboardingVC.pageControl.currentPageIndicatorTintColor = UIColor.white
        onboardingVC.skipButton.setTitleColor(UIColor.white, for: .normal)
        onboardingVC.allowSkipping = true
        
        onboardingVC.skipHandler = {
            self.skip()
        }
        
        return onboardingVC
        
    }
    func handleOnboardingCompletion (){
        self.setupNormalRootViewController()
    }
    
    func setupNormalRootViewController (){
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.window = UIWindow(frame: UIScreen.main.bounds)
        let mainStoryboard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let startVC = mainStoryboard.instantiateViewController(withIdentifier: "firstStart") as! ConsentViewController
        appDelegate.window?.rootViewController = startVC
        appDelegate.window?.makeKeyAndVisible()
    }
    
    func skip (){
        self.setupNormalRootViewController()
        
    }
}

