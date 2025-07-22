package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
@Config
public class Robot {
    long curTime;
    public boolean isAuto;
    public String Mode="Home";
    Drive drive;
    Deposit deposit;
    Intake intake;
    //TODO needs tuning
    public static int slidesIntakePos=500;
    public long startClawClose;
    public int clawCloseDelay=1000;
    public static int HighBasketPos=700;
    public int SlidesAdjust = 0;
    public long specimenIntakeStartTime=0;
    public int specimenIntakeDelay=2000;
    public int specimenOutakeDelay=2000;
    public long startSpecimenOutakeTime = 0;
    public int specimenPullSlideDownDelay = 1000;
    public long startPullSlideDownDelay = 0;
    public long startOpenClaw = 0;
    public long OpenClawDelay = 1000;
    public long autoSlidesExtendStartTime;
    public int autoSlidesExtendDelay = 500;
    public long autoIntakeExtendStartTime;
    public int autoIntakeExtendDelay = 500;
    public long retractingToHomeStartTime;
    public int retractingToHomeDelay=3000;
    //aprovals
    public boolean clawCloseApproval=false;
    public boolean depositClawApproval=false;
    public boolean depositReady=false;
    public boolean specimenIntakeClawApproval=false;
    public boolean pullSlideDownApproval = false;
    public boolean diddyFun = false;
    public boolean homeRetracting=false;
    public boolean retractedHome=false;
    public long startDepClawOpenTime = 0;
    public int armRetractDelay = 2000;
    public boolean waitingToRetractArm = false;

    public Robot(LinearOpMode op, boolean isAuto){
        drive = new Drive(op);
        intake = new Intake(op);
        deposit = new Deposit(op);
        this.isAuto=isAuto;
    }
    public void UpdateRobot(){
        curTime=System.currentTimeMillis(); // Makes curTime the current time in milliseconds.
        switch (Mode){ // Switch statement
            case "Home": // Definition for the default robot state: "Home", essentially just reseting everything
                intake.clawClose(); // Closes intake claw
                deposit.retract(curTime);
                intake.retract(curTime);
                intake.wristTransfer();
                deposit.depArmTransfer();
                deposit.DepRotateTransfer();
                deposit.DepClawOpen();
                SlidesAdjust = 0; // Resets slides
                startClawClose=curTime; // Starts claw close delay
                specimenIntakeStartTime = curTime; // Starts specimen intake delay
                autoIntakeExtendStartTime = curTime; // Starts auto intake delay
                if(isAuto&&homeRetracting){ // Tells the robot that home rectrac    ting is done
                    if(curTime-retractingToHomeStartTime>retractingToHomeDelay){
                        homeRetracting=false;
                        retractedHome=true;
                    }
                }
                break;
            case "intake": // Sets an "intake" state for the robot
                intake.clawOpen(); // Self-explanatory
                intake.extend((slidesIntakePos + SlidesAdjust),curTime); // Extend the slides
                intake.wristIntaking();
                if(!clawCloseApproval){ // If the approval to allow the claw to be closed returns false
                    startClawClose=curTime; // Start the timer till the driver can close the claw
                }else{
                    intake.clawClose(); // Close the claw
                    if(curTime-startClawClose>clawCloseDelay){ // If delay is over
                        Mode="Home"; // Return the robot to the default state
                        clawCloseApproval=false; // Reset approval
                    }
                }

                break;
            case "outake": // Sets an "outake" state for the robot
                intake.extend(slidesIntakePos,curTime); // Extends the intake slide
                intake.wristIntaking(); // Rotates the claw
                if(intake.getSlidePos()>slidesIntakePos-40){ // Checks if the slides are correct height, opens the claw
                    intake.clawOpen();

                }else{
                    startOpenClaw=curTime; // Starts the delay for opening the claw
                }
                if(curTime-startOpenClaw>OpenClawDelay){ // Resets robot once claw has been open
                    Mode="Home";
                }
                break;
            case "deposit": // Sets a "deposit" state for the robot
                if(!depositReady&&intake.slidesStalled&&deposit.slidesStalled){  // not even i know what the heck slidesstalled means
                    deposit.DepClawClose(); // Closes the deposit claw
                    intake.clawOpen(); // Opens the intake claw
                    if(curTime-startClawClose>clawCloseDelay){ // Checks if the claw close delay is fully passed, then tells the robot that the deposit is ready
                        depositReady=true;
                    }
                }else if(depositReady){ // Checks if deposit is already ready
                    deposit.extend(HighBasketPos+SlidesAdjust,curTime); // Extends the slides to high basket level
                    deposit.depArmDeposit(); // Makes the deposit arm deposit the sample
                    deposit.DepRotateDeposit(); // Rotates the deposit arm
                    if(depositClawApproval){ // If the deposit claw is ready
                        deposit.DepClawOpen(); // Open the deposit claw
                    }else { // else
                        startClawClose=curTime; // Start the claw close delay
                    }
                }else{ // else
                    startClawClose=curTime; // Start the claw close delay
                }

                if(depositClawApproval && curTime - startClawClose > clawCloseDelay){
                    if (!waitingToRetractArm) {
                        deposit.DepClawOpen();
                        startDepClawOpenTime = curTime;
                        waitingToRetractArm = true;
                    } else if (curTime - startDepClawOpenTime > armRetractDelay) {
                        deposit.depArmTransfer();
                        Mode = "Home";
                        depositReady = false;
                        depositClawApproval = false;
                        waitingToRetractArm = false;
                    }
                }
                break;
            case "SpecimenIntake": // Makes a new robot state: "SpecimenIntake"
                deposit.extend(deposit.slidesSpecimenIntake, curTime); // Extends deposit to get ready
                intake.retract(curTime); // Retracts intake slide
                deposit.specimenIntake(); // Runs specimen intake method
                if(curTime-specimenIntakeStartTime>specimenIntakeDelay) { // If specimen intake delay is over
                    if (specimenIntakeClawApproval) { // If specimen intake claw approval is affirmative
                        deposit.DepClawClose(); // Close the deposit claw
                    }
                }
                if(!specimenIntakeClawApproval){ // If specimen intake claw is negative
                    startClawClose = curTime; // Start the delay for closing the claw
                }
                else if(curTime-startClawClose>clawCloseDelay){ // If claw close delay is finished
                    Mode=("SpecimenOutake"); // Set the robot mode to SpecimenOutake
                    specimenIntakeClawApproval = false; // Resets approval
                    startSpecimenOutakeTime = curTime; // Starts specimen outake delay
                }
                break;
            case "SpecimenOutake": // Makes a new robot state: "SpecimenOutake"
                deposit.extend(deposit.slidesSpecimenOutaking, curTime); // Extends the slide for outake
                deposit.specimenOutake(); // Runs the method specimenOutake()
                if (curTime - startSpecimenOutakeTime > specimenOutakeDelay) { // If specimen outake delay is finished
                    if (!pullSlideDownApproval) { // If pull slide down approval is negative
                        startPullSlideDownDelay = curTime; // Start the pull slide down delay
                    }
                    else  { // else
                        if (curTime - startPullSlideDownDelay > specimenPullSlideDownDelay) { // If specimen pull slide down delay is finished
                            deposit.DepClawOpen(); // Opens the deposit claw
                            diddyFun = true; // 2nd claw approval is affirmative
                        }
                        if (!diddyFun) { // If the 2nd claw is negative
                            startOpenClaw = curTime; // Starts the open claw delay
                        }
                        else if (curTime - startOpenClaw > OpenClawDelay) { // else if open claw delay is finished
                            pullSlideDownApproval = false; // Resets pull slide down approval
                            diddyFun = false; // Resets 2nd claw approval delay
                            Mode = "Home"; // Resets robot state
                        }

                    }
                }
                break;
            case "autoDeposit":
                if(!depositReady&&intake.slidesStalled&&deposit.slidesStalled){
                    deposit.DepClawClose();
                    intake.clawOpen();
                    if(curTime-startClawClose>clawCloseDelay){
                        depositReady=true;
                        autoSlidesExtendStartTime = curTime;

                    }
                }else if(depositReady){
                    deposit.extend(HighBasketPos,curTime);
                    deposit.depArmDeposit();
                    deposit.DepRotateDeposit();
                    if(curTime- autoSlidesExtendStartTime > autoSlidesExtendDelay){
                        deposit.DepClawOpen();
                    }else {
                        startClawClose=curTime;
                    }
                }else{
                    startClawClose=curTime;
                }

                if (depositClawApproval && curTime - startClawClose > clawCloseDelay) {
                    if (!waitingToRetractArm) {
                        deposit.DepClawOpen(); // Open the claw
                        startDepClawOpenTime = curTime; // Start delay timer
                        waitingToRetractArm = true;
                    } else if (curTime - startDepClawOpenTime > armRetractDelay) {
                        deposit.depArmTransfer(); // Move arm out of the way
                        Mode = "Home";
                        depositReady = false;
                        depositClawApproval = false;
                        waitingToRetractArm = false;
                        retractingToHomeStartTime = curTime;
                        homeRetracting = true;
                        retractedHome = false;
                    }
                }
                break;
            case "autoIntake":
                intake.clawOpen();
                intake.extend(slidesIntakePos,curTime);
                intake.wristIntaking();
                if(curTime-autoIntakeExtendStartTime>autoIntakeExtendDelay) {
                    intake.clawClose();
                    if(!clawCloseApproval){
                        startClawClose=curTime;
                        clawCloseApproval=true;
                    }
                    if (clawCloseApproval && curTime - startClawClose > clawCloseDelay) {
                        Mode = "Home";
                        clawCloseApproval = false;
                        retractingToHomeStartTime=curTime;
                        homeRetracting=true;
                        retractedHome=false;
                    }
                }
                break;
        }
        intake.updateSlides(curTime);
        deposit.updateSlides(curTime);
    }
}
