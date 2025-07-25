package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Intake.slideHomePos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Robot {
    long curTime;
    public boolean isAuto;
    public String Mode="Home";
    public Drive drive;
    public Deposit deposit;
    public Intake intake;
    //TODO needs tuning
    public static int slidesIntakePos=9300;
    public long startClawClose;
    public static int clawCloseDelay=500;
    public static int HighBasketPos=4095;//tuned
    public int SlidesAdjust = 0;
    public long specimenIntakeStartTime=0;
    public static int specimenIntakeDelay=2000;
    public static int specimenOutakeDelay=2000;
    public long startSpecimenOutakeTime = 0;
    public int specimenPullSlideDownDelay = 2000;
    public long startPullSlideDownDelay = 0;
    public long startOpenClaw;
    public static int OpenClawDelay = 700;
    public long autoSlidesExtendStartTime;
    public static int autoSlidesExtendDelay = 2000;
    public long autoIntakeExtendStartTime;
    public static int autoIntakeExtendDelay = 2000;
    public long retractingToHomeStartTime;
    public static int retractingToHomeDelay=3000;
    public long startArmRetractTime;
    public static int armRetractDelay = 900;
    public long depSpecArmStartTime;
    public int depSpecArmDelay=1000;
    public  static int slideParkPosAdjustment =-3000;
    //aprovals
    public boolean clawCloseApproval=false;
    public boolean depositClawApproval=false;
    public boolean depositReady=false;
    public boolean specimenIntakeClawApproval=false;
    public boolean specimenOpenClawApproval = false;
    public boolean diddyFun = false;
    public boolean homeRetracting=false;//auto
    public boolean retractedHome=false;//auto
    public boolean waitingToRetractArm = false;
    public boolean specimenDepositArmApproval=false;


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
                intake.extend(slideHomePos+SlidesAdjust,curTime);
                intake.wristTransfer();
                deposit.depArmTransfer();
                deposit.DepRotateTransfer();
                deposit.DepClawOpen();
                 // Resets slides
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
                if(!clawCloseApproval){ // If the approval to allow the claw to be closed returns false
                    startClawClose=curTime; // Start the timer till the driver can close the claw
                }else{
                    intake.clawClose(); // Close the claw
                    if(curTime-startClawClose>clawCloseDelay){ // If delay is over
                        Mode="Home"; // Return the robot to the default state
                        clawCloseApproval=false;
                        SlidesAdjust = 0;
                    }
                }

                break;
            case "outake": // Sets an "outake" state for the robot
                intake.extend(slidesIntakePos+SlidesAdjust,curTime); // Extends the intake slide
                intake.wristIntaking(); // Rotates the claw
                if(intake.slideIsAtPos(slidesIntakePos)){ // Checks if the slides are correct height, opens the claw
                    intake.clawOpen();
                    intake.wristTransfer();
                }else{
                    startOpenClaw=curTime; // Starts the delay for opening the claw
                }
                if(curTime-startOpenClaw>OpenClawDelay){ // Resets robot once claw has been open
                    Mode="Home";
                    SlidesAdjust = 0;
                }
                break;
            case "deposit": // Sets a "deposit" state for the robot
                if(!depositReady){  // not even i know what the heck slidesstalled means
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
                        startOpenClaw=curTime; // Start the claw close delay
                    }
                }else{ // else
                    startOpenClaw=curTime;// Start the claw close delay
                    startClawClose=curTime;
                }

                if(depositReady&&depositClawApproval && curTime - startOpenClaw > OpenClawDelay){
                    if (!waitingToRetractArm) {
                        deposit.depArmTransfer();
                        deposit.DepRotateTransfer();
                        startArmRetractTime = curTime;
                        waitingToRetractArm = true;
                    } else if (curTime - startArmRetractTime > armRetractDelay) {
                        Mode = "Home";
                        depositReady = false;
                        depositClawApproval = false;
                        waitingToRetractArm = false;
                        SlidesAdjust = 0;
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
                if (curTime - startSpecimenOutakeTime > specimenOutakeDelay) {
                    if(specimenDepositArmApproval) {
                        deposit.depArmDepositSpecimen();
                        if(curTime-depSpecArmStartTime>depSpecArmDelay) {
                            if (specimenOpenClawApproval) {
                                deposit.DepClawOpen(); // Opens the deposit claw
                                diddyFun = true;
                            }
                            if (!diddyFun) {
                                startOpenClaw = curTime; // Starts the open claw delay
                            } else if (curTime - startOpenClaw > OpenClawDelay) {
                                specimenOpenClawApproval = false;
                                diddyFun = false;
                                specimenDepositArmApproval=false;
                                Mode = "Home"; // Resets robot state
                            }
                        }
                    }else{
                        depSpecArmStartTime=curTime;
                    }
                    /*else  {
                        deposit.extend(deposit.slidesSpecimenDeposit,curTime);
                        if (curTime - startPullSlideDownDelay > specimenPullSlideDownDelay) { // If specimen pull slide down delay is finished
                            deposit.DepClawOpen(); // Opens the deposit claw
                            diddyFun = true; // 2nd claw approval is affirmative
                        }
                        if (!diddyFun) { // If the 2nd claw is negative
                            startOpenClaw = curTime; // Starts the open claw delay
                        }
                        else if (curTime - startOpenClaw > OpenClawDelay) { // else if open claw delay is finished
                            specimenOpenClawApproval = false; // Resets pull slide down approval
                            diddyFun = false; // Resets 2nd claw approval delay
                            Mode = "Home"; // Resets robot state
                        }

                    }*/
                }
                break;
            case "autoDeposit":
                if(!depositReady&&intake.slideIsAtPos(slideHomePos)&&deposit.slideIsAtPos(deposit.HomePos)){
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
                    if(curTime - autoSlidesExtendStartTime > autoSlidesExtendDelay){
                        deposit.DepClawOpen();
                        depositClawApproval=true;
                    }else {
                        startOpenClaw=curTime;
                    }
                }else{
                    startOpenClaw=curTime;
                    startClawClose=curTime;
                }
                if (depositClawApproval && curTime - startOpenClaw > OpenClawDelay) {
                    if (!waitingToRetractArm) {
                        deposit.depArmTransfer();
                        deposit.DepRotateTransfer();
                        startArmRetractTime = curTime; // Start delay timer
                        waitingToRetractArm = true;
                    } else if (curTime - startArmRetractTime > armRetractDelay) {
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
