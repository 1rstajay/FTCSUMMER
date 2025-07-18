package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp(name="Drive", group="TeleOp")
public class Drive{
    private DcMotorEx FrontRight;
    private DcMotorEx FrontLeft;
    private DcMotorEx BackRight;
    private DcMotorEx BackLeft;
    private LinearOpMode op;
    public static double strafingOffest=1;
    public Drive(LinearOpMode op){
        this.op=op;

        HardwareMap hardwareMap = op.hardwareMap;
        FrontRight = op.hardwareMap.get(DcMotorEx.class,"FrontRight");
        FrontLeft = op.hardwareMap.get(DcMotorEx.class,"FrontLeft");
        BackRight = op.hardwareMap.get(DcMotorEx.class,"BackRight");
        BackLeft = op.hardwareMap.get(DcMotorEx.class,"BackLeft");

        FrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRight.setDirection(DcMotorSimple.Direction.REVERSE);


    }
    public void driveInputs(double x, double y, double theta){
        x = x * strafingOffest;
        double FR = (y-x-theta);
        double BR = (y+x-theta);
        double FL = (y+x+theta);
        double BL = (y-x+theta);
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
