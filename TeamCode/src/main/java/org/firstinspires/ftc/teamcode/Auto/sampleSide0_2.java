package org.firstinspires.ftc.teamcode.Auto;

import static java.lang.Math.PI;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RoadRunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Robot;

@Config
@Autonomous(name="0+2")
public class sampleSide0_2 extends LinearOpMode {
    Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
    Robot robot;
    public static double startposX=-10;
    public static double startposY=-61;
    public static double startposHead=PI/2;
    public static double preDepX=-52;
    public static double preDepY=-52;
    public static double preDepHead=PI/4;
    public static double intake1X=-48;
    public static double intake1Y=-42;
    public static double intake1Head=PI/2;
    public static double Dep1X=-52;
    public static double Dep1Y=-52;
    public static double Dep1Head=(5*PI)/4;

    public class AutoDepositAction implements Action {
        public boolean initialized = false;
        public boolean stillRunning=true;

        @Override
        public boolean run(TelemetryPacket telemetryPacket) {
            if (!initialized) {
                robot.Mode = "autoDeposit";
                initialized = true;
            }
            robot.UpdateRobot();
            if(robot.Mode.equals("Home")&& robot.retractedHome){
                stillRunning=false;
            }
            return stillRunning;
        }
    }
    public class AutoIntakeAction implements Action{
        public boolean initialized = false;
        boolean stillRunning=true;
        @Override
        public boolean run(TelemetryPacket telemetryPacket) {
            if (!initialized) {
                robot.Mode = "autoIntake";
                initialized = true;
            }
            robot.UpdateRobot();
            if(robot.Mode.equals("Home")&& robot.retractedHome){
                stillRunning=false;
            }
            return stillRunning;

        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(this, true);

        Pose2d startPos = new Pose2d(startposX, startposY, startposHead);
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);

        // Actions are now properly chained and use the custom deposit action
        Action driveToPreload = drive.actionBuilder(startPos)
                .strafeToConstantHeading(new Vector2d(-30, -56))
                .strafeToLinearHeading(new Vector2d(preDepX, preDepY), preDepHead)
                .waitSeconds(1)
                .build();

        Pose2d depositPreloadPose = new Pose2d(preDepX, preDepY, preDepHead);
        Action driveToIntake1 = drive.actionBuilder(depositPreloadPose)
                .splineTo(new Vector2d(intake1X, intake1Y), intake1Head)
                .build();

        Pose2d intake1Pose = new Pose2d(intake1X, intake1Y, intake1Head);
        Action driveToDeposit1 = drive.actionBuilder(intake1Pose)
                .setReversed(true)
                .splineTo(new Vector2d(Dep1X, Dep1Y), Dep1Head)
                .build();


        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        driveToPreload,
                        new AutoDepositAction(),
                        driveToIntake1,
                        new AutoIntakeAction(),
                        driveToDeposit1,
                        new AutoDepositAction()
                )
        );

    }
}
