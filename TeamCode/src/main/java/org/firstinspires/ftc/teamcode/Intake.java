package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
@Config

public class Intake {
    private Servo intakeClaw;//pretty obvious

    //rotate servo is thing that rotates the claw
    //wrist servos move claw up and down
    private Servo intakeWrist;//left from bot perspective

    //slide motors
    private DcMotorEx intakeSlide;//left and right also from bot perspective



    public static double clawOpenPos=0.46;
    public static double clawClosePos=0.6;
    public static double wristIntakePos=1;
    public static double wristTransferPos=0.4;

//TODO tuned positions again mb
    public static double kp=0.01; //tune
    public static double ki=0; // not very useful
    public static double kd=0;
    private int errorChange;
    private int lastError=0;
    private int errorSum=0;
    public static int slideHomePos=0;//this is the fully rectracted pos; tuned
    public static int targetheight=slideHomePos;
    public static int Maxheight=9500;//tuned
    private long startTime;
    public boolean extending = false;
    public static double StallPower=-0.3;
    public static double stallRange=30;
    public static double stallCurrent=11;
    public boolean slidesStalled=false;


    public Intake(LinearOpMode op){

        intakeClaw = op.hardwareMap.get(Servo.class,("intakeClaw"));
        intakeWrist = op.hardwareMap.get(Servo.class,("intakeWrist"));
        intakeSlide = op.hardwareMap.get(DcMotorEx.class,("intakeSlide"));

        intakeSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    //claw methods
    public void clawOpen(){
        intakeClaw.setPosition(clawOpenPos);
    }
    public void clawClose(){
        intakeClaw.setPosition(clawClosePos);
    }
    //wrist methods
    public void wristIntaking(){
        intakeWrist.setPosition(wristIntakePos);

    }
    public void wristTransfer(){
        intakeWrist.setPosition(wristTransferPos);
    }
    //rotate
    //PID(more like PD) and slide stuff
    public double PID(int currPos, int targetPos,double time){
        targetPos=Math.min(targetPos,Maxheight);
        int error = targetPos-currPos;
        if(time==0){
            time=1;
        }
        double errorChange = (error-lastError)/time;
        lastError=error;
        errorSum+=error*time;
        return ((kp*error)+(ki*errorSum)+(kd*errorChange));
    }
    public void extend(int targetPos,long time){
        startTime=time;
        errorSum=0;
        lastError=0;
        targetheight=Math.min(targetPos,Maxheight);
        extending=true;
        slidesStalled=false;
    }
    public void retract(long time){
        startTime=time;
        errorSum=0;
        lastError=0;
       targetheight=slideHomePos;
        extending=false;
    }
    public void updateSlides(long time){
        double TIME=time-startTime;
        startTime=time;
        double power=PID(getSlidePos(),targetheight,TIME);
        intakeSlide.setPower(power);
        if(!slidesStalled&&!extending&&slidesCurrent()>stallCurrent){
            slidesStalled=true;
            intakeSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        }
    }
    public int getSlidePos(){
        int pos=(intakeSlide.getCurrentPosition());
        return pos;
    }
    public double slidesCurrent(){
        return(intakeSlide.getCurrent(CurrentUnit.AMPS));
    }
    public boolean slideIsAtPos(int pos){
        if(Math.abs(getSlidePos()-pos)<30){
            return true;
        }else{
            return false;
        }
    }
    public void manualEncoderReset(){
        intakeSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
