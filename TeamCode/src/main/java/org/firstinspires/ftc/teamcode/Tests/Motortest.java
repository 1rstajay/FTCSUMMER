package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Deposit;
import org.firstinspires.ftc.teamcode.Intake;

@Config
@TeleOp(name = "motorTest")
public class Motortest extends LinearOpMode{
    Intake intake;
    Deposit deposit;
    public static boolean depslidesTest=false;
    public static boolean depslidesTestRetract=false;
    public static boolean intakeslidesTest=false;
    public static boolean intakeslidesTestRetract=false;
    public static boolean on=false;
    public static int target=500;
    public static double armPos=0.3;
    @Override
    public void runOpMode() {
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        //Motor1 = hardwareMap.get(DcMotorEx.class,Motor_1);
        //Motor2 = hardwareMap.get(DcMotorEx.class,Motor_2);
        intake = new Intake(this);
        deposit = new Deposit(this);
        armPos=deposit.armDeposit;
        waitForStart();
        while(opModeIsActive()){
            deposit.depArmSync(armPos);
            if(depslidesTest){
                deposit.extend(target,System.currentTimeMillis());
                deposit.updateSlides(System.currentTimeMillis());
            }else if(depslidesTestRetract){
                deposit.retract(System.currentTimeMillis());
                deposit.updateSlides(System.currentTimeMillis());
            }
            if(intakeslidesTest){
                intake.extend(target,System.currentTimeMillis());
                intake.updateSlides(System.currentTimeMillis());
            }else if(intakeslidesTestRetract){
                intake.retract(System.currentTimeMillis());
                intake.updateSlides(System.currentTimeMillis());
            }
            if(on){
                //Motor1.setPower(power1);
                //Motor2.setPower(power2);
            }

            telemetry.addData("Target Position", target);
            telemetry.addData("current dep pos: ",deposit.slidesPos());
            telemetry.addData("current intake pos: ", intake.getSlidePos());
            telemetry.addData("dep slides voltage: ",deposit.slidesCurrent());
            telemetry.addData("intake slides voltage: ",intake.slidesCurrent());
            telemetry.addData("is Deposit stalled in: ",deposit.slidesStalled);
            telemetry.addData("is intake stalled in: ",intake.slidesStalled);
            telemetry.update();
        }
    }
}
