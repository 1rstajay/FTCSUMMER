package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Deposit;
import org.firstinspires.ftc.teamcode.Intake;


public class Motortest extends LinearOpMode{
    private LinearOpMode op;
    Intake intake;
    Deposit deposit;
    private DcMotorEx Motor1;
    private DcMotorEx Motor2;
    public static double power1=0;
    public static double power2=0;
    public static boolean depslidesTest=false;
    public static boolean depslidesTestRetract=false;
    public static boolean intakeslidesTest=false;
    public static boolean intakeslidesTestRetract=false;
    public static boolean on=false;
    public static int target=200;
    @Override
    public void runOpMode() {
        Motor1 = op.hardwareMap.get(DcMotorEx.class,"Motor1");
        Motor2 = op.hardwareMap.get(DcMotorEx.class,"Motor2");
        intake = new Intake(this);
        deposit = new Deposit(this);
        waitForStart();
        while(opModeIsActive()){

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
        }
    }
}
