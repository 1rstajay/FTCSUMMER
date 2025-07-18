package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class Robot {
    long curTime;
    String Mode="Home";
    Drive drive;
    Deposit deposit;
    Intake intake;
    public static int slidesIntakePos=500;
    public long startClawClose;
    public boolean clawCloseApproval=false;
    public int clawCloseDelay=600;
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
                intake.rotateTransfer();
                deposit.depArmTransfer();
                deposit.DepRotateTransfer();
                deposit.DepClawOpen();
                intake.slidesAdjust=0;
                intake.adjustableRotate=0;
            break;
            case "intake":
                intake.clawOpen();
                intake.extend((slidesIntakePos + intake.slidesAdjust),curTime);
                intake.wristIntaking();
                intake.rotate(intake.intakeRotatePos + intake.adjustableRotate);
                if(!clawCloseApproval){
                    startClawClose=curTime;
                }
                if(clawCloseApproval&&curTime-startClawClose>clawCloseDelay){
                    Mode="Home";
                    clawCloseApproval=false;
                }

        intake.updateSlides(curTime);
        deposit.updateSlides(curTime);
        }
    }
}
