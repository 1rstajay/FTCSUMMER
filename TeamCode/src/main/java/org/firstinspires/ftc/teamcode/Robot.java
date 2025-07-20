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
                deposit.extend(deposit.slidesSpecimenIntake,curTime);
                intake.retract(curTime);
                deposit.specimenIntake();






        }
        intake.updateSlides(curTime);
        deposit.updateSlides(curTime);
    }
}
