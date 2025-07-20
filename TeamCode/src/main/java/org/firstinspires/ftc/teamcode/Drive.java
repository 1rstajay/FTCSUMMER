package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
@Config
public class Drive{
    private DcMotorEx FrontRight;
    private DcMotorEx FrontLeft;
    private DcMotorEx BackRight;
    private DcMotorEx BackLeft;
    public double FR=0.0;
    public double FL=0.0;
    public double BR=0.0;
    public double BL=0.0;
    public static double strafingOffest=1;
    public Drive(LinearOpMode op){
        FrontRight = op.hardwareMap.get(DcMotorEx.class,"FrontRight");
        FrontLeft = op.hardwareMap.get(DcMotorEx.class,"FrontLeft");
        BackRight = op.hardwareMap.get(DcMotorEx.class,"BackRight");
        BackLeft = op.hardwareMap.get(DcMotorEx.class,"BackLeft");

        FrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRight.setDirection(DcMotorSimple.Direction.REVERSE);


    }
    public void driveInputs(double x, double y, double theta){
        x = x * strafingOffest;
        FR = (y-x-theta);
        BR = (y+x-theta);
        FL = (y+x+theta);
        BL = (y-x+theta);
        double max = Math.max(Math.abs(FR),Math.max(Math.abs(BR),Math.max(Math.abs(FL),Math.abs(BL))));
        if (max>1){
            FR = FR/max;
            BR = BR/max;
            FL = FL/max;
            BL = BL/max;
        }
        FrontRight.setPower(FR);
        FrontLeft.setPower(FL);
        BackRight.setPower(BR);
        BackLeft.setPower(BL);
    }

}
