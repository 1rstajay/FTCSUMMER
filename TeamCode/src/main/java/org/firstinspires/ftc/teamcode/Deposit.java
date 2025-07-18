package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Deposit {
    //Servos
    private Servo depClaw;//the claw is well the claw
    private Servo depArmL;//the arm servos are the 2 servos moving the whole claw and stuff from one side to another
    private Servo depArmR;// btw left and right from bots perspective
    private Servo depRotate;//rotates the claw

    //slide motors
    private DcMotorEx depLeftSlide;
    private DcMotorEx depRightSlide;

    //Servo positions
    //TODO needs tuning
    public static double depClawOpen=0.5;
    public static double depClawClose=0.5;
    public static double RotateTransfer=0.5;
    public static double RotateDeposit=0.5;
    public static double armOffset=0.0;
    public static double armTransfer=0.5;
    public static double armDeposit=0.5;
    // PID&Slides stuff
    //TODO needs tuning pls
    private int lastError=0;
    private int ErrorSum=0;
    public static double kp=0.0;
    public static double ki=0.0;//not very useful
    public static double kd=0.0;
    public static int HomePos=0;
    public static long startTime;
    private int targetHeight=HomePos;
    public static int maxHeight=1000;

    public Deposit(LinearOpMode op){
        depClaw = op.hardwareMap.get(Servo.class,"depClaw");
        depArmL = op.hardwareMap.get(Servo.class,"depArmL");
        depArmR = op.hardwareMap.get(Servo.class,"depArmR");
        depRotate = op.hardwareMap.get(Servo.class,"depRotate");
        depLeftSlide = op.hardwareMap.get(DcMotorEx.class,"depLeftSlide");
        depRightSlide = op.hardwareMap.get(DcMotorEx.class,"depRightSlide");

        depRightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        depLeftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //TODO might need to reverse a slide motors so they run in the same direction so pls check
        //depRightSlide.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    //Servos
    public void DepClawOpen(){
        depClaw.setPosition(depClawOpen);
    }
    public void DepClawClose(){
        depClaw.setPosition(depClawClose);
    }
    public void DepRotateTransfer(){
        depRotate.setPosition(RotateTransfer);
    }
    public void DepRotateDeposit(){
        depRotate.setPosition(RotateDeposit);
    }
    private void depArmSync(double pos){//to sync servos
        depArmL.setPosition(pos);
        depArmR.setPosition((1-pos)+armOffset);
    }
    public void depArmTransfer(){
        depArmSync(armTransfer);
    }
    public void depArmDeposit(){
        depArmSync(armDeposit);
    }
    //slides and PID
    private double PID(int initPos,int targetPos,long time){
        int Error = targetPos-initPos;
        double errorChange=(Error-lastError)/time;
        ErrorSum+=(Error*time);
        lastError=Error;
        return ((kp*Error)+(ki*ErrorSum)+(kd*errorChange));
    }
    public int slidesPos(){
        int pos = (depLeftSlide.getCurrentPosition()+depRightSlide.getCurrentPosition())/2;
        return pos;
    }
    public void extend(int targetPos,long time){
        ErrorSum=0;
        lastError=0;
        startTime=time;
        targetHeight=Math.min(targetPos,maxHeight);
    }
    public void retract(long time){
        ErrorSum=0;
        lastError=0;
        startTime=time;
        targetHeight=HomePos;
    }
    public void updateSlides(long time){
        long TIME=time-startTime;
        startTime=time;
        double power=PID(slidesPos(),targetHeight,TIME);
        depLeftSlide.setPower(power);
        depRightSlide.setPower(power);
    }

}
