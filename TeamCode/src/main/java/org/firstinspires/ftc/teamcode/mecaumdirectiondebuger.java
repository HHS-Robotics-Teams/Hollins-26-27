package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Simple Mecanum Motor Direction Debugger.
 * Press buttons to spin individual drive motors at low power (0.25).
 */
@TeleOp(name = "Mecanum Direction Debugger", group = "Testing")
public class mecaumdirectiondebuger extends OpMode {

    private DcMotorEx frontLeft, backLeft, backRight, frontRight;
    private final double TEST_POWER = 0.25; // Low test power

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontleft");
        backLeft = hardwareMap.get(DcMotorEx.class, "backleft");
        backRight = hardwareMap.get(DcMotorEx.class, "backright");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontright");

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Right side motors reversed for standard drive kinematics
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        // Press button / D-Pad to turn on individual motor at low power (0.25)
        frontLeft.setPower((gamepad1.x || gamepad1.dpad_up) ? TEST_POWER : 0);
        backLeft.setPower((gamepad1.y || gamepad1.dpad_down) ? TEST_POWER : 0);
        frontRight.setPower((gamepad1.b || gamepad1.dpad_right) ? TEST_POWER : 0);
        backRight.setPower((gamepad1.a || gamepad1.dpad_left) ? TEST_POWER : 0);

        // Right Trigger: Test ALL 4 motors forward together at low power
        if (gamepad1.right_trigger > 0.1) {
            frontLeft.setPower(TEST_POWER);
            backLeft.setPower(TEST_POWER);
            frontRight.setPower(TEST_POWER);
            backRight.setPower(TEST_POWER);
        }

        // Live telemetry output
        telemetry.addData("X / D-Pad Up", "Front Left  (\"frontleft\")  Power: %.2f | Enc: %d", frontLeft.getPower(), frontLeft.getCurrentPosition());
        telemetry.addData("Y / D-Pad Down", "Back Left   (\"backleft\")   Power: %.2f | Enc: %d", backLeft.getPower(), backLeft.getCurrentPosition());
        telemetry.addData("B / D-Pad Right", "Front Right (\"frontright\") Power: %.2f | Enc: %d", frontRight.getPower(), frontRight.getCurrentPosition());
        telemetry.addData("A / D-Pad Left", "Back Right  (\"backright\")  Power: %.2f | Enc: %d", backRight.getPower(), backRight.getCurrentPosition());
        telemetry.addData("Right Trigger", "Test ALL 4 Motors Forward @ 0.25 Power");
        telemetry.update();
    }
}
