package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp (name = "Teleop")
@Config
public class Teleop extends LinearOpMode {
    /*
    press 'a' while in home to go to intake
    then press left bumper to get sample and go back to home(dpad up&down can adjust slide)
    also can u dpad left and right to move wrist down and up respectively
    x in home to outake
    b in home to go to deposit
    dpad up and down in deposit to adjust slides
    left bumper in deposit to open claw and drop sample and go back to home
    press both triggers at the same time to reset everything and go back to home in case u get stuck in some software issue
     */
    Robot robot;
    @Override
    public void runOpMode() throws InterruptedException {
        String Controls="press 'a' while in home to go to intake\n" +
                "    then press left bumper to get sample and go back to home(dpad up&down can adjust actuator)\n" +
                "    also u can press dpad left and right to move wrist down and up respectively\n" +
                "    x in home to outake\n" +
                "    b in home to go to deposit\n" +
                "    dpad up and down in deposit to adjust slides\n" +
                "    left bumper in deposit to open claw and drop sample and go back to home\n" +
                "    press both triggers at the same time to reset everything and go back to home in case u get stuck in some software issue";
        robot=new Robot(this,false);
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.UpdateRobot();
        waitForStart();
        while(opModeIsActive()){
            if(gamepad2.a){
                robot.Mode.equals("deposit");
                robot.SlidesAdjust=robot.slideParkPosAdjustment;
            }
            if(gamepad1.left_trigger>0.3&&gamepad1.right_trigger>0.3){//reset button
                robot.Mode="Home";
                robot.clawCloseApproval=false;
                robot.depositClawApproval=false;
                robot.depositReady=false;
                robot.specimenIntakeClawApproval=false;
                robot.specimenOpenClawApproval = false;
                robot.diddyFun = false;
                robot.waitingToRetractArm = false;
                robot.specimenDepositArmApproval=false;
            }
            robot.drive.driveInputs(gamepad1.left_stick_x, -gamepad1.left_stick_y,gamepad1.right_stick_x);
            if(gamepad1.a&&robot.Mode.equals("Home")){
                robot.Mode="intake";
            }
            if(robot.Mode.equals("Home")) {
                if (gamepad1.dpad_up) {
                    robot.SlidesAdjust += 30;
                } else if (gamepad1.dpad_down) {
                    robot.SlidesAdjust -= 30;
                }
            }
            if(robot.Mode.equals("intake")){
                if(gamepad1.dpad_up){
                    robot.SlidesAdjust +=30;
                }else if(gamepad1.dpad_down){
                    robot.SlidesAdjust -=30;
                }
                if(gamepad1.dpad_left){
                    robot.intake.wristIntaking();
                }
                if(gamepad1.dpad_right){
                    robot.intake.wristTransfer();
                }

                if(gamepad1.left_bumper){
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
            if(robot.Mode.equals("SpecimenOutake")&& gamepad1.left_bumper)
            if(robot.Mode.equals("SpecimenOutake") && gamepad1.right_bumper) {
                robot.specimenOpenClawApproval = true;
            }

            robot.UpdateRobot();
            telemetry.addData("state",robot.Mode);
            telemetry.addData("depslides pos",robot.deposit.slidesPos());
            telemetry.addData("intake slides pos",robot.intake.getSlidePos());
            telemetry.addData("depositReady", robot.depositReady);
            telemetry.addData("Controls",Controls);
            telemetry.update();
        }
    }
}
