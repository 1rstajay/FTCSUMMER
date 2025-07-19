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
    private DcMotorEx Motor1;
    public static String Motor_1="";
    private DcMotorEx Motor2;
    public static String Motor_2="";
    public static double power1=0;
    public static double power2=0;
    public static boolean depslidesTest=false;
    public static boolean depslidesTestRetract=false;
    public static boolean intakeslidesTest=false;
    public static boolean intakeslidesTestRetract=false;
    public static boolean on=false;
    public static int target=200;
    public static double kp=0.0;
    public static double ki=0.0;
    public static double kd=0.0;
    @Override
    public void runOpMode() {
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        Motor1 = hardwareMap.get(DcMotorEx.class,Motor_1);
        Motor2 = hardwareMap.get(DcMotorEx.class,Motor_2);
        intake = new Intake(this);
        deposit = new Deposit(this);
        waitForStart();
        while(opModeIsActive()){
            deposit.kp=kp;
            deposit.ki=ki;
            deposit.kd=kd;
            intake.kp=kp;
            intake.ki=ki;
            intake.kd=kd;
            if(depslidesTest){
                deposit.extend(target,System.currentTimeMillis());
            }else if(depslidesTestRetract){
                deposit.retract(System.currentTimeMillis());
            }
            if(intakeslidesTest){
                intake.extend(target,System.currentTimeMillis());
            }else if(intakeslidesTestRetract){
                intake.retract(System.currentTimeMillis());
            }
            if(on){
                Motor1.setPower(power1);
                Motor2.setPower(power2);
            }
            telemetry.addData("Motor1 Power", power1);
            telemetry.addData("Motor2 Power", power2);
            telemetry.addData("Target Position", target);
            telemetry.addData("current dep pos: ",deposit.slidesPos());
            telemetry.addData("current intake pos: ", intake.getSlidePos());
            telemetry.update();
        }
    }
}
