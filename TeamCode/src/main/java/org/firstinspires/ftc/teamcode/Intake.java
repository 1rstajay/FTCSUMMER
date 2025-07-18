package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {
    private Servo intakeClaw;//pretty obvious

    //rotate servo is thing that rotates the claw
    private Servo intakeRotate;

    //wrist servos move claw up and down
    private Servo intakeLeftWrist;//left from bot perspective
    private Servo intakeRightWrist;//right from bot perspective

    //slide motors
    private DcMotorEx intakeLeftSlide;//left and right also from bot perspective
    private DcMotorEx intakeRightSlide;


    //TODO needs tuning
    public static double clawOpenPos=0.5;
    public static double clawClosePos=0.5;
    public static double wristIntakePos=0.5;
    public static double wristOffest=0.0;
    public static double wristTransferPos=0.5;
    public static double rotateTransfer=0.5;
    public static double intakeRotatePos=0.5;
    public double adjustableRotate=0.5;
    public int slidesAdjust=0;

    //TODO PID stuff

    double kp=0;
    double ki=0; // not very useful
    double kd=0;
    private int errorChange;
    private int lastError=0;
    private int errorSum=0;
    public static int slideHomePos=0;//this is the fully rectracted pos;
    public static int targetheight=slideHomePos;
    public static int Maxheight=10000;//def needs to be tune es muy importanto
    private double startTime;

    public Intake(LinearOpMode op){

        intakeClaw = op.hardwareMap.get(Servo.class,("intakeClaw"));
        intakeRotate = op.hardwareMap.get(Servo.class,("intakeRotate"));
        intakeLeftWrist = op.hardwareMap.get(Servo.class,("intakeLeftWrist"));
        intakeRightWrist = op.hardwareMap.get(Servo.class,("intakeRightWrist"));
        intakeLeftSlide = op.hardwareMap.get(DcMotorEx.class,("intakeLeftSlide"));
        intakeRightSlide = op.hardwareMap.get(DcMotorEx.class,("intakeRightSlide"));

        intakeLeftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeRightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    //claw methods
    public void clawOpen(){
        intakeClaw.setPosition(clawOpenPos);
    }
    public void clawClose(){
        intakeClaw.setPosition(clawClosePos);
    }
    //wrist methods
    private void wristSync(double pos){
        intakeLeftWrist.setPosition(pos);
        intakeRightWrist.setPosition((1-pos)+wristOffest);
    }
    public void wristIntaking(){
        wristSync(wristIntakePos);

    }
    public void wristTransfer(){
        wristSync(wristTransferPos);
    }
    //rotate
    public void rotate(double pos){
        intakeRotate.setPosition(pos);// prob gonna use joystick to control this or smth
    }
    public void rotateTransfer(){
        intakeRotate.setPosition(rotateTransfer);
    }
    //PID(more like PD) and slide stuff
    public double PID(int currPos, int targetPos,double time){
        int error = targetPos-currPos;
        double errorChange = (error-lastError)/time;
        lastError=error;
        errorSum+=error*time;
        return ((kp*error)+(ki*errorSum)+(kd*errorChange));
    }
    public void extend(int targetPos,double time){
        startTime=time;
        errorSum=0;
        lastError=0;
        targetheight=Math.min(targetPos,Maxheight);
    }
    public void retract(double time){
        startTime=time;
        errorSum=0;
        lastError=0;
       targetheight=slideHomePos;
    }
    public void updateSlides(double time){
        double TIME=time-startTime;
        startTime=time;
        double power=PID(getSlidePos(),targetheight,TIME);
        intakeRightSlide.setPower(power);
        intakeLeftSlide.setPower(power);
    }
    public int getSlidePos(){
        int pos=((intakeLeftSlide.getCurrentPosition()+ intakeRightSlide.getCurrentPosition())/2);
        return pos;
    }

}
