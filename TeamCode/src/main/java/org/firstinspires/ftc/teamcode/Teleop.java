package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp (name = "Teleop")
public class Teleop extends LinearOpMode {
    Robot robot;
    @Override
    public void runOpMode() throws InterruptedException {
        robot=new Robot(this,0,0,0);

        isStarted();
        while(opModeIsActive()){
            if(gamepad1.a&&gamepad1.dpad_left){//Override
                robot.Mode="Home";
                robot.clawCloseApproval=false;
                robot.depositClawApproval=false;
                robot.depositReady=false;
                robot.depositReady=false;
            }
            robot.drive.driveInputs(gamepad1.left_stick_x, -gamepad1.left_stick_y,gamepad1.right_stick_x);
            if(gamepad1.a&&robot.Mode.equals("Home")){
                robot.Mode="intake";
            }
            if(robot.Mode.equals("intake")){
                if(gamepad1.dpad_up){
                    robot.SlidesAdjust +=10;
                }else if(gamepad1.dpad_down){
                    robot.SlidesAdjust -=10;
                }
                if(gamepad1.dpad_left){
                    robot.intake.adjustableRotate+=0.05;
                }
                if(gamepad1.dpad_right){
                    robot.intake.adjustableRotate-=0.05;
                }
                if(gamepad1.right_bumper){
                    robot.clawCloseApproval=true;
                }
            }
            if(gamepad1.b&&robot.Mode.equals("Home")){
                robot.Mode="deposit";
            }
            if(robot.Mode.equals("deposit")){
                if(gamepad1.dpad_up){
                    robot.SlidesAdjust +=10;
                }else if(gamepad1.dpad_down){
                    robot.SlidesAdjust -=10;
                }
                if(gamepad1.left_bumper){
                    robot.depositClawApproval=true;
                }
            }
            robot.UpdateRobot();
        }
    }
}
