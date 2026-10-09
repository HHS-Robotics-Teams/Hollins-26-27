package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Components {
    public static DcMotor frontLeftWheelMotor;
    public static DcMotor frontRightWheelMotor;
    public static DcMotor backLeftWheelMotor;
    public static DcMotor backRightWheelMotor;
    public static DcMotor intakeMotor;
    public static DcMotor launcherMotor;

    public static void initComponents(HardwareMap hardwareMap) {
        frontLeftWheelMotor = hardwareMap.get(DcMotor.class, "frontLeftWheelMotor");
        frontRightWheelMotor = hardwareMap.get(DcMotor.class, "frontRightWheelMotor");
        backLeftWheelMotor = hardwareMap.get(DcMotor.class, "backLeftWheelMotor");
        backRightWheelMotor = hardwareMap.get(DcMotor.class, "backRightWheelMotor");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        launcherMotor = hardwareMap.get(DcMotor.class, "launcherMotor");

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
    }
}
