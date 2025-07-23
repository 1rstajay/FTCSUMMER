package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Drive;

@Config
@TeleOp(name="driveTest")
public class driveTest extends LinearOpMode {
    Drive drive;

    @Override
    public void runOpMode() throws InterruptedException {
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        drive=new Drive(this);
        waitForStart();
        while(opModeIsActive()){
            drive.driveInputs(gamepad1.left_stick_x, -gamepad1.left_stick_y,gamepad1.right_stick_x);
            telemetry.addData("FR Power", drive.FR);
            telemetry.addData("FL Power", drive.FL);
            telemetry.addData("BR Power", drive.BR);
            telemetry.addData("BL Power", drive.BL);
            telemetry.update();
        }
    }
}
