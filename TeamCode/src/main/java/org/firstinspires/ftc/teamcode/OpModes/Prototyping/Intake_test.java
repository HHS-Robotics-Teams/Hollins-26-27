package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Intake_test", group = "Testing")
public class Intake_test extends OpMode {

    private DcMotor intakeFront;
    private DcMotor conveyor;
    private Servo kicker;

    private double kickerActivePosition = 1.0;
    private final double kickerRestPosition = 0.0;

    private boolean dpadUpLast = false;
    private boolean dpadDownLast = false;

    @Override
    public void init() {

        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setDirection(DcMotorSimple.Direction.REVERSE);

        conveyor = hardwareMap.get(DcMotor.class, "conveyor");
        conveyor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        conveyor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        conveyor.setDirection(DcMotorSimple.Direction.REVERSE);

        kicker = hardwareMap.get(Servo.class, "kicker");
        kicker.setPosition(kickerRestPosition);
    }

    @Override
    public void loop() {
        // --- Adjust Kicker Active Position (D-Pad Up / Down) ---
        if (gamepad1.dpad_up && !dpadUpLast) {
            kickerActivePosition = Math.min(1.0, kickerActivePosition + 0.05);
        }
        dpadUpLast = gamepad1.dpad_up;

        if (gamepad1.dpad_down && !dpadDownLast) {
            kickerActivePosition = Math.max(0.0, kickerActivePosition - 0.05);
        }
        dpadDownLast = gamepad1.dpad_down;

        // --- Front Intake & Conveyor Control ---
        double intakePower = 0.0;
        if (gamepad1.right_trigger > 0.1) {
            intakePower = gamepad1.right_trigger;
        } else if (gamepad1.left_trigger > 0.1) {
            intakePower = -gamepad1.left_trigger;
        }

        intakeFront.setPower(intakePower);
        conveyor.setPower(intakePower);

        // --- Kicker Servo Control ---
        if (gamepad1.right_bumper) {
            kicker.setPosition(kickerActivePosition);
        } else {
            kicker.setPosition(kickerRestPosition);
        }

        // --- Telemetry ---
        telemetry.addData("Intake Power", intakeFront.getPower());
        telemetry.addData("Conveyor Power", conveyor.getPower());
        telemetry.addData("Kicker Servo Position", kicker.getPosition());
        telemetry.addData("Target Active Position", "%.2f (Adjust: D-Pad Up/Down)", kickerActivePosition);
        telemetry.update();
    }
}
