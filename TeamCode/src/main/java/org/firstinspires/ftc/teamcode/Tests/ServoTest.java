package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

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
    private LinearOpMode op;
    HardwareMap hardwareMap = op.hardwareMap;
    @Override
    public void runOpMode() {
        Servo1 = op.hardwareMap.get(Servo.class,Servo_1);
        Servo2 = op.hardwareMap.get(Servo.class,Servo_2);
        waitForStart();
        while(opModeIsActive()){
            if(sync){
                Servo1.setPosition(syncpos);
                Servo2.setPosition((1-syncpos)+offset);
            }else {
                Servo1.setPosition(pos1);
                Servo2.setPosition(pos2);
            }
        }
    }
}
