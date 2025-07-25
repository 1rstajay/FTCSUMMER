package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Drive;
import org.firstinspires.ftc.teamcode.Robot;


@Config
@Autonomous(name="0+1")
public class sampleAuto extends LinearOpMode {
    Robot robot;
    Drive drive;
    public long curTime;
    public static int slidesDelay=4000;
    public static int driveToBasket=1000;
    public static int driveOut=2000;
    public static int clawDepositDelay=2000;
    public static int puttingSpecOn=2000;

    @Override
    public void runOpMode() throws InterruptedException {
        robot=new Robot(this,true);
        drive=new Drive(this);
        robot.deposit.DepClawClose();
        robot.intake.wristTransfer();
        robot.intake.clawOpen();
        robot.deposit.depArmTransfer();
        robot.deposit.DepRotateTransfer();
        waitForStart();
        robot.deposit.depArmDeposit();
        robot.deposit.DepRotateDeposit();
        robot.deposit.DepClawClose();
        curTime=System.currentTimeMillis();
        while(System.currentTimeMillis()-curTime<slidesDelay&&opModeIsActive()){
            robot.deposit.extend(robot.HighBasketPos,System.currentTimeMillis());
            robot.UpdateRobot();
        }
        curTime=System.currentTimeMillis();
        while(System.currentTimeMillis()-curTime<driveToBasket&&opModeIsActive()){//moving straight to basket
            robot.drive.driveInputs(0,0.1,0);
            robot.UpdateRobot();
        }
        //strafing to basket
       /* while(System.currentTimeMillis()-curTime<driveToBasket&&opModeIsActive()){
            robot.drive.driveInputs(-0.1,0,0);
            robot.UpdateRobot();
        }*/
        curTime=System.currentTimeMillis();
        while(System.currentTimeMillis()-curTime<clawDepositDelay&&opModeIsActive()) {
            robot.drive.driveInputs(0, 0, 0);
            robot.deposit.DepClawOpen();
            robot.UpdateRobot();
        }
        curTime=System.currentTimeMillis();
        while(System.currentTimeMillis()-curTime<driveOut&&opModeIsActive()){
            robot.drive.driveInputs(0,0.1,0);
            robot.UpdateRobot();
        }
        curTime=System.currentTimeMillis();
        while(System.currentTimeMillis()-curTime<slidesDelay&&opModeIsActive()) {
            robot.deposit.retract(System.currentTimeMillis());
            robot.deposit.depArmTransfer();
            robot.deposit.DepRotateTransfer();
            robot.deposit.DepClawOpen();
            robot.UpdateRobot();
        }
        robot.deposit.manualEncoderReset();
        /*curTime=System.currentTimeMillis();
        while(curTime-System.currentTimeMillis()<slidesDelay&&opModeIsActive()) {
            robot.deposit.extend(robot.deposit.slidesSpecimenOutaking + 50, System.currentTimeMillis());
        }
        robot.drive.driveInputs(0,-0.3,0);
        curTime=System.currentTimeMillis();
        while(curTime-System.currentTimeMillis()<driveTOSub&&opModeIsActive()){
            robot.drive.driveInputs(0,-0.3,0);
        }
        while(curTime-System.currentTimeMillis()<slidesDelay&&opModeIsActive()) {
            robot.deposit.extend(robot.deposit.slidesSpecimenOutaking-10, System.currentTimeMillis());
        }
        robot.drive.driveInputs(0,0.1,0);
        curTime=System.currentTimeMillis();
        while(curTime-System.currentTimeMillis()<slidesDelay&&opModeIsActive()) {
            robot.deposit.extend(robot.deposit.slidesSpecimenOutaking-10, System.currentTimeMillis());
        }*/



    }
}
