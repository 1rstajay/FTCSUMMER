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
    //TODO needs tuning
    public static double wristIntakePos=0.5;
    public static double wristTransferPos=0.6;


    public static double kp=0.01; //tuned
    public static double ki=0; // not very useful
    public static double kd=0;
    private int errorChange;
    private int lastError=0;
    private int errorSum=0;
    public static int slideHomePos=-10500;//this is the fully rectracted pos; tuned
    public static int targetheight=slideHomePos;
    public static int Maxheight=-890;//tuned
    private long startTime;
    public boolean retracting=false;

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
    }
    public void retract(long time){
        startTime=time;
        errorSum=0;
        lastError=0;
       targetheight=slideHomePos;
        retracting=true;
    }
    public void updateSlides(long time){
        double TIME=time-startTime;
        startTime=time;
        double power=PID(getSlidePos(),targetheight,TIME);
        intakeSlide.setPower(power);
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

}
