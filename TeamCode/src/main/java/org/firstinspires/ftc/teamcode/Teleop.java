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
            robot.drive.driveInputs(gamepad1.left_stick_x, -gamepad1.left_stick_y,gamepad1.right_stick_x);
            if(gamepad1.dpad_up&&robot.Mode.equals("Home")){
                robot.Mode="intake";
            }
            if(robot.Mode.equals("intake")){
                if(gamepad1.dpad_up){
                    robot.intake.slidesAdjust+=10;
                }else if(gamepad1.dpad_down){
                    robot.intake.slidesAdjust-=10;
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
            robot.UpdateRobot();
        }
    }
}
