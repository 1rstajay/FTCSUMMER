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


    //TODO needs tuning
    public static double clawOpenPos=0.5;
    public static double clawClosePos=0.5;
    public static double wristIntakePos=0.5;
    public static double wristOffest=0.0;
    public static double wristTransferPos=0.5;


    //TODO PID stuff

    public static double kp=0;
    public static double ki=0; // not very useful
    public static double kd=0;
    private int errorChange;
    private int lastError=0;
    private int errorSum=0;
    public static int slideHomePos=0;//this is the fully rectracted pos;
    public static int targetheight=slideHomePos;
    public static int Maxheight=10000;//def needs to be tune es muy importanto
    private long startTime;
    public boolean retracting=false;
    public static int stallOffset=60;

    public static double StallPower=-0.3;
    public static double holdStallPower=-0.1;
    public static double stallRange=60;
    public int drift=0;
    public double stallCurrent=3;
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
        int error = targetPos-currPos;
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
        double power=PID(getSlidePos(),targetheight+drift,TIME);
        intakeSlide.setPower(power);
        if(!slidesStalled&&retracting&&getSlidePos()<(slideHomePos+stallRange)){
            intakeSlide.setPower(StallPower);
            if(slidesCurrent()>stallCurrent){
                retracting=false;
                slidesStalled=true;
                drift+=slideHomePos-getSlidePos();
            }
        }else if(slidesStalled){
            intakeSlide.setPower(holdStallPower);

        }
    }
    public int getSlidePos(){
        int pos=(intakeSlide.getCurrentPosition());
        return pos;
    }
    public double slidesCurrent(){
        return(intakeSlide.getCurrent(CurrentUnit.AMPS));
    }

}
