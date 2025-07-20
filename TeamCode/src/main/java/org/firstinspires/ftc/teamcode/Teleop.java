package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp (name = "Sample Teleop")
@Config
public class Teleop extends LinearOpMode {
    Robot robot;
    @Override
    public void runOpMode() throws InterruptedException {
        robot=new Robot(this,0,0,0);
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());

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
            telemetry.addData("state: ",robot.Mode);
        }
    }
}
