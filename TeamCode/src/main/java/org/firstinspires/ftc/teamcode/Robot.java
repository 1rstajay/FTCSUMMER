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

    public Robot(LinearOpMode op, boolean isAuto){
        drive = new Drive(op);
        intake = new Intake(op);
        deposit = new Deposit(op);
        this.isAuto=isAuto;
    }
    public void UpdateRobot(){
        curTime=System.currentTimeMillis();
        switch (Mode){
            case "Home":
                intake.clawClose();
                deposit.retract(curTime);
                intake.retract(curTime);
                intake.wristTransfer();
                deposit.depArmTransfer();
                deposit.DepRotateTransfer();
                deposit.DepClawOpen();
                SlidesAdjust = 0;
                startClawClose=curTime;
                specimenIntakeStartTime = curTime;
                autoIntakeExtendStartTime = curTime;
                if(isAuto&&homeRetracting){
                    if(curTime-retractingToHomeStartTime>retractingToHomeDelay){
                        homeRetracting=false;
                        retractedHome=true;
                    }
                }
            break;
            case "intake":
                intake.clawOpen();
                intake.extend((slidesIntakePos + SlidesAdjust),curTime);
                intake.wristIntaking();
                if(!clawCloseApproval){
                    startClawClose=curTime;
                }else{
                    intake.clawClose();
                    if(curTime-startClawClose>clawCloseDelay){
                        Mode="Home";
                        clawCloseApproval=false;
                    }
                }

            break;
            case "outake":
                intake.extend(slidesIntakePos,curTime);
                intake.wristIntaking();
                if(intake.getSlidePos()>slidesIntakePos-40){
                    intake.clawOpen();

                }else{
                    startOpenClaw=curTime;
                }
                if(curTime-startOpenClaw>OpenClawDelay){
                    Mode="Home";
                }
                break;
            case "deposit":
                if(!depositReady&&intake.slidesStalled&&deposit.slidesStalled){
                    deposit.DepClawClose();
                    intake.clawOpen();
                    if(curTime-startClawClose>clawCloseDelay){
                        depositReady=true;
                    }
                }else if(depositReady){
                        deposit.extend(HighBasketPos+SlidesAdjust,curTime);
                        deposit.depArmDeposit();
                        deposit.DepRotateDeposit();
                        if(depositClawApproval){
                            deposit.DepClawOpen();
                        }else {
                            startClawClose=curTime;
                        }
                }else{
                    startClawClose=curTime;
                }

                if(depositClawApproval&&curTime-startClawClose>clawCloseDelay){
                    Mode="Home";
                    depositReady=false;
                    depositClawApproval=false;
                }
                break;
            case "SpecimenIntake":
                    deposit.extend(deposit.slidesSpecimenIntake, curTime);
                    intake.retract(curTime);
                    deposit.specimenIntake();
                if(curTime-specimenIntakeStartTime>specimenIntakeDelay) {
                    if (specimenIntakeClawApproval) {
                        deposit.DepClawClose();
                    }
                }
                if(!specimenIntakeClawApproval){
                    startClawClose = curTime;
                }
                else if(curTime-startClawClose>clawCloseDelay){
                    Mode=("SpecimenOutake");
                    specimenIntakeClawApproval = false;
                    startSpecimenOutakeTime = curTime;
                }
            case "SpecimenOutake":
                deposit.extend(deposit.slidesSpecimenOutaking, curTime);
                deposit.specimenOutake();
                if (curTime - startSpecimenOutakeTime > specimenOutakeDelay) {
                    if (!pullSlideDownApproval) {
                    startPullSlideDownDelay = curTime;
                    }
                    else  {
                        if (curTime - startPullSlideDownDelay > specimenPullSlideDownDelay) {
                            deposit.DepClawOpen();
                            diddyFun = true;
                        }
                        if (!diddyFun) {
                            startOpenClaw = curTime;
                        }
                        else if (curTime - startOpenClaw > OpenClawDelay) {
                            pullSlideDownApproval = false;
                            diddyFun = false;
                            Mode = "Home";
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

                if(depositClawApproval&&curTime-startClawClose>clawCloseDelay){
                    Mode="Home";
                    depositReady=false;
                    depositClawApproval=false;
                    retractingToHomeStartTime=curTime;
                    homeRetracting=true;
                    retractedHome=false;
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
