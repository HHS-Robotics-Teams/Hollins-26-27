package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Components {
    public static DcMotor leftFront;
    public static DcMotor rightFront;
    public static DcMotor leftRear;
    public static DcMotor rightRear;
    public static DcMotor intakeFront;
    public static CRServo servoFeedLeft1;
    public static CRServo servoFeedRight1;
    public static CRServo servoFeedLeft2;
    public static CRServo servoFeedRight2;
    public static DcMotor intakeRear;
    public static DcMotor spindexer;
    public static Servo flowerFront;
    public static Servo shootingServo;

    public static void initComponents(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotor.class, "leftfront");
        rightFront = hardwareMap.get(DcMotor.class, "rightfront");
        leftRear = hardwareMap.get(DcMotor.class, "leftrear");
        rightRear = hardwareMap.get(DcMotor.class, "rightrear");

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightRear.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeFront= hardwareMap.get(DcMotor.class,"intakefront");
        intakeFront.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        servoFeedLeft1= hardwareMap.get(CRServo.class, "servofeedleft1");
        servoFeedLeft1.setDirection(CRServo.Direction.REVERSE);

        servoFeedRight1= hardwareMap.get(CRServo.class, "servofeedright1");
        servoFeedRight1.setDirection(CRServo.Direction.REVERSE);

        servoFeedLeft2= hardwareMap.get(CRServo.class, "servofeedleft2");
        servoFeedLeft2.setDirection(CRServo.Direction.REVERSE);

        servoFeedRight2= hardwareMap.get(CRServo.class, "servofeedright2");
        servoFeedRight2.setDirection(CRServo.Direction.REVERSE);


    }
}
