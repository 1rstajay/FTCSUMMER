package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Deposit;
import org.firstinspires.ftc.teamcode.Intake;

@Config
@TeleOp(name = "ResetEncoders")
public class ResetEncoders extends LinearOpMode {
    Intake intake;
    Deposit deposit;

    public static boolean resetDepSlides = false;
    public static boolean resetIntakeSlides = false;

    @Override
    public void runOpMode() {
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        intake = new Intake(this);
        deposit = new Deposit(this);

        waitForStart();

        while (opModeIsActive()) {
            deposit.manualEncoderReset();
            intake.manualEncoderReset();
            if (resetDepSlides) {
                resetDepSlides = false;
                deposit.manualEncoderReset();
            }
            if (resetIntakeSlides) {
                resetIntakeSlides = false;
                intake.manualEncoderReset();
            }
            telemetry.addData("current dep pos: ",deposit.slidesPos());
            telemetry.addData("current intake pos: ", intake.getSlidePos());
            telemetry.update();
        }
    }
}
