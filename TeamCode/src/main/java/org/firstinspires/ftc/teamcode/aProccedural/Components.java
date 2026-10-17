package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Components {
    public static DcMotor frontLeft;
    public static DcMotor frontRight;
    public static DcMotor backLeft;
    public static DcMotor backRight;
    public static DcMotor turret;
    public static DcMotor launcher;
    public static DcMotor intakeFront;
    public static CRServo leftFeedRoller;
    public static CRServo rightFeedRoller;
    public static CRServo kicker;


    public static void initComponents(HardwareMap hardwareMap) {
        //Initialize Drive Motors
        frontLeft = hardwareMap.get(DcMotor.class, "frontleft");
        frontRight = hardwareMap.get(DcMotor.class, "frontright");
        backLeft = hardwareMap.get(DcMotor.class, "backleft");
        backRight = hardwareMap.get(DcMotor.class, "backright");

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //Initialize Turret motor
//        turret = hardwareMap.get(DcMotor.class, "turret");
//        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        turret.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize Launcher motor
        launcher = hardwareMap.get(DcMotor.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize intake motor
        intakeFront = hardwareMap.get(DcMotor.class, "intakefront");
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setDirection(DcMotorSimple.Direction.FORWARD);

        // Initialize Feed Rollers
        leftFeedRoller= hardwareMap.get(CRServo.class,"leftroller");
        rightFeedRoller = hardwareMap.get(CRServo.class, "rightroller");
        leftFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        kicker = hardwareMap.get(CRServo.class,"kicker");
        kicker.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);

    }
}
