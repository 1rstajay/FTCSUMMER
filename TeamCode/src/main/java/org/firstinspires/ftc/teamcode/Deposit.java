package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
@Config
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

    public static double depClawOpen=0.46;
    public static double depClawClose=0.61;
    public static double RotateTransfer=0.82;
    public static double RotateDeposit=0.15;
    public static double armOffset=0.0;
    public static double armTransfer=0.83; // tuned
    public static double armDeposit=0.3;//tuned
    public static double armSpecimenDepositPos=0.2;
    public static double armSpecimentIntakePos=0.23;
    public static double rotateSpecimenIntake=0.65;
    public static double rotateSpecimenOutake=0.15;
    public static double armSpecimenOutake=0.23;
    // PID&Slides stuff
    //TODO needs tuning pls
    public int slidesSpecimenIntake=400;
    public int slidesSpecimenOutaking=2850;
    public int slidesSpecimenDeposit=900;
    private int lastError=0;
    private int ErrorSum=0;
    public static double kp=0.01;//tuned
    public static double ki=0.0;//not very useful
    public static double kd=0.0;
    public static int HomePos=-30;

    public  long startTime;
    private int targetHeight=HomePos;
    public static int maxHeight=4100;
    public boolean extending =false;
    public static int stallOffset=60;

    public static double StallPower=-0.3;
    public static double stallRange=30;
    public static double stallCurrent=11;
    public boolean slidesStalled=false;

    public Deposit(LinearOpMode op){
        depClaw = op.hardwareMap.get(Servo.class,"depClaw");
        depArmL = op.hardwareMap.get(Servo.class,"depArmL");
        depArmR = op.hardwareMap.get(Servo.class,"depArmR");
        depRotate = op.hardwareMap.get(Servo.class,"depRotate");
        depLeftSlide = op.hardwareMap.get(DcMotorEx.class,"depLeftSlide");
        depRightSlide = op.hardwareMap.get(DcMotorEx.class,"depRightSlide");

        depRightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        depLeftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        depRightSlide.setDirection(DcMotorSimple.Direction.REVERSE);

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
    public void specimenOutake() {
        depClaw.setPosition(depClawClose);
        depRotate.setPosition(rotateSpecimenOutake);
        depArmSync(armSpecimenOutake);
    }
    public void specimenIntake(){
        depClaw.setPosition(depClawOpen);
        depRotate.setPosition(rotateSpecimenIntake);
        depArmSync(armSpecimentIntakePos);
    }
    public void depArmSync(double pos){//to sync servos
        depArmL.setPosition(pos);
        depArmR.setPosition((1-pos)+armOffset);
    }
    public void depArmTransfer(){
        depArmSync(armTransfer);
    }
    public void depArmDeposit(){
        depArmSync(armDeposit);
    }
    public void depArmDepositSpecimen(){
        depArmSync(armSpecimenDepositPos);
    }
    //slides and PID
    private double PID(int initPos,int targetPos,long time){
        targetPos=Math.min(targetPos,maxHeight);
        int Error = targetPos-initPos;
        if(time==0){
            time=1;
        }
        double errorChange=(Error-lastError)/time;
        ErrorSum+=(Error*time);
        lastError=Error;
        return ((kp*Error)+(ki*ErrorSum)+(kd*errorChange));
    }
    public int slidesPos(){
        int pos = (depLeftSlide.getCurrentPosition()+depRightSlide.getCurrentPosition())/2;
        return pos;
    }
    public double slidesCurrent(){
        return(depLeftSlide.getCurrent(CurrentUnit.AMPS)+depRightSlide.getCurrent(CurrentUnit.AMPS));
    }
    public void extend(int targetPos,long time){
        ErrorSum=0;
        lastError=0;
        startTime=time;
        targetHeight=Math.min(targetPos,maxHeight);
        extending=true;
        slidesStalled=false;
    }
    public void retract(long time){
        extending=false;
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
        if(!slidesStalled&&!extending && slidesCurrent()>stallCurrent){
            slidesStalled=true;
            depLeftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            depRightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            depLeftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            depRightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }
    public boolean slideIsAtPos(int pos){
        if(Math.abs(slidesPos()-pos)<60){
            return true;
        }else{
            return false;
        }
    }
    public void manualEncoderReset(){
        depLeftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        depRightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        depLeftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        depRightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

}
