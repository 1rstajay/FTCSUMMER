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
    /* CONTROLS
    * gamepad1.a and gamepad1.dpad_left to reset state to home in case smth is stuck
    * gamepad1.a when in home state is to move to intake mode
    * in intake dpad up down move the slide up and down
    * in intake right bumper is to close claw on a sample and intake slide in and return to home
    * gamepad1.b when in home is to go to deposit
    * in deposit dpad up down move the slide up and down
    * in deposit left bumper to release sample
    * gamepad1.y in home goes to intake specimen position
    * press y again to close claw on specimen and go into hang position
    * while in specimenOutake press dpad down to drop slides and hang specimen and return to home
    * while in home press gamepad1.x to outake sample
    *
    *
    *
    *
    *
    *
    *
    * */
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
                robot.specimenIntakeClawApproval=false;
                robot.pullSlideDownApproval = false;
                robot.diddyFun = false;
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
            if(gamepad1.x&&robot.Mode.equals("Home")){
                robot.Mode="outake";
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
            if(gamepad1.y&&robot.Mode.equals("Home")){
                robot.Mode="SpecimenIntake";
            }
            if(robot.Mode.equals("SpecimenIntake")) {
              if(gamepad1.y) {
                  robot.specimenIntakeClawApproval=true;
              }
            }
            if(robot.Mode.equals("SpecimenOutake") && gamepad1.dpad_down) {
                robot.pullSlideDownApproval = true;
            }

            robot.UpdateRobot();
            telemetry.addData("state: ",robot.Mode);
        }
    }
}
