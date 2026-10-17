package org.firstinspires.ftc.teamcode.aProccedural;

<<<<<<< HEAD
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Base64;

public class Components {
    public static DcMotor frontLeftWheelMotor;
    public static DcMotor frontRightWheelMotor;
    public static DcMotor backLeftWheelMotor;
    public static DcMotor backRightWheelMotor;
    public static DcMotor intakeMotor;
    public static DcMotor launcherMotor;
    private static DigitalChannel odometryPod;

    public static void initComponents(HardwareMap hardwareMap) {
        frontLeftWheelMotor = hardwareMap.get(DcMotor.class, "frontLeftWheelMotor");
        frontRightWheelMotor = hardwareMap.get(DcMotor.class, "frontRightWheelMotor");
        backLeftWheelMotor = hardwareMap.get(DcMotor.class, "backLeftWheelMotor");
        backRightWheelMotor = hardwareMap.get(DcMotor.class, "backRightWheelMotor");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        launcherMotor = hardwareMap.get(DcMotor.class, "launcherMotor");
        odometryPod = hardwareMap.get(DigitalChannel.class, "odometryPod");

        frontLeftWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backLeftWheelMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftWheelMotor.setDirection(DcMotor.Direction.REVERSE);
    }
    private void setPowers(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        double maxSpeed = 1.0;
        maxSpeed = Math.max(maxSpeed, Math.abs(frontLeftPower));
        maxSpeed = Math.max(maxSpeed, Math.abs(frontRightPower));
        maxSpeed = Math.max(maxSpeed, Math.abs(backLeftPower));
        maxSpeed = Math.max(maxSpeed, Math.abs(backRightPower));

        frontLeftPower /= maxSpeed;
        frontRightPower /= maxSpeed;
        backLeftPower /= maxSpeed;
        backRightPower /= maxSpeed;

        frontLeftWheelMotor.setPower(frontLeftPower);
        frontRightWheelMotor.setPower(frontRightPower);
        backLeftWheelMotor.setPower(backLeftPower);
        backRightWheelMotor.setPower(backRightPower);
    }
    public void drive(double forward, double right, double rotate) {
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backLeftPower = forward - right + rotate;
        double backRightPower = forward + right - rotate;

        setPowers(frontLeftPower, frontRightPower, backLeftPower, backRightPower);
=======
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
        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turret.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize Launcher motor
        launcher = hardwareMap.get(DcMotor.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize intake motor
        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize Feed Rollers
        leftFeedRoller= hardwareMap.get(CRServo.class,"leftRoller");
        rightFeedRoller = hardwareMap.get(CRServo.class, "rightRoller");
        kicker = hardwareMap.get(CRServo.class,"kicker");

        leftFeedRoller.setDirection(DcMotorSimple.Direction.REVERSE);
>>>>>>> 8c59d54ac5ec9de852d80fae0375e8fa21f1352d
    }
}
