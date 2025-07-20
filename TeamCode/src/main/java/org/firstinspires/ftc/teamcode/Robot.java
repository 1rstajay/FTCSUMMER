package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
@Config
public class Robot {
    long curTime;
    String Mode="Home";
    Drive drive;
    Deposit deposit;
    Intake intake;
    //TODO needs tuning
    public static int slidesIntakePos=500;
    public long startClawClose;
    public int clawCloseDelay=600;
    public static int HighBasketPos=700;
    public int SlidesAdjust = 0;
    //aprovals
    public boolean clawCloseApproval=false;
    public boolean depositClawApproval=false;
    public boolean depositReady=false;
    public boolean specimenIntakeClawApproval=false;
    public long specimenIntakeStartTime=0;
    public int specimenIntakeDelay=600;
    public int specimenOutakeDelay=1200;
    public long startSpecimenOutakeTime = 0;
    public int specimenPullSlideDownDelay = 600;
    public long startPullSlideDownDelay = 0;
    public long startOpenClawForAttachingDelay = 0;
    public long specimenOpenClawForAttachingDelay = 250;
    public boolean startClawA = false;
    public boolean pullSlideDownApproval = false;

    public boolean diddyFun = false;
    public Robot(LinearOpMode op, double x, double y,double theta){
        drive = new Drive(op);
        intake = new Intake(op);
        deposit = new Deposit(op);
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

            break;
            case "intake":
                intake.clawOpen();
                intake.extend((slidesIntakePos + SlidesAdjust),curTime);
                intake.wristIntaking();
                if(!clawCloseApproval){
                    startClawClose=curTime;
                }
                if(clawCloseApproval&&curTime-startClawClose>clawCloseDelay){
                    Mode="Home";
                    clawCloseApproval=false;
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
                if(curTime-startClawClose>clawCloseDelay){
                    Mode=("SpecimenOutake");
                    specimenIntakeClawApproval = false;
                    startSpecimenOutakeTime = curTime;
                }
            case "SpecimenOutake":
                deposit.extend(deposit.slidesSpecimenOutaking, curTime);
                deposit.specimenOutake();
                if (curTime - startSpecimenOutakeTime > specimenOutakeDelay) {
                    deposit.extend(deposit.slidesSpecimenDeposit, curTime);

                }
                if(!pullSlideDownApproval) startPullSlideDownDelay = curTime;
                if (pullSlideDownApproval) {
                    if (curTime - startPullSlideDownDelay > specimenPullSlideDownDelay) {
                        deposit.DepClawOpen();
                        diddyFun = true;
                    }
                    if(!diddyFun){
                        startOpenClawForAttachingDelay = curTime;
                    }
                    if (curTime - startOpenClawForAttachingDelay > specimenOpenClawForAttachingDelay) {
                        pullSlideDownApproval = false;
                        diddyFun = false;
                        Mode="Home";


                    }

                }

        }
        intake.updateSlides(curTime);
        deposit.updateSlides(curTime);
    }
}
