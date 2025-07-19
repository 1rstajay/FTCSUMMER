package org.firstinspires.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "servoTest")
@Config
public class ServoTest extends LinearOpMode {
    private Servo Servo1;
    public static String Servo_1="";
    private Servo Servo2;
    public static String Servo_2="";
    public static double pos1=0.0;
    public static double pos2=0.0;
    public static double syncpos=0.0;
    public static boolean sync=false;
    public static double offset=0.0;
    @Override
    public void runOpMode() {
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        Servo1 = hardwareMap.get(Servo.class,Servo_1);
        Servo2 = hardwareMap.get(Servo.class,Servo_2);
        waitForStart();
        while(opModeIsActive()){
            if(sync){
                Servo1.setPosition(syncpos);
                Servo2.setPosition((1-syncpos)+offset);
            }else {
                Servo1.setPosition(pos1);
                Servo2.setPosition(pos2);
            }
            telemetry.addData("Servo1 Position", Servo1.getPosition());
            telemetry.addData("Servo2 Position", Servo2.getPosition());
            telemetry.addData("syncpos", syncpos);
            telemetry.addData("sync", sync);
            telemetry.addData("offset", offset);
            telemetry.update();
        }
    }
}
