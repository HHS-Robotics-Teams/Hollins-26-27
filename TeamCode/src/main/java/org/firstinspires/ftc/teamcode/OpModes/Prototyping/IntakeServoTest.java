package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.depollenatorLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.depollenatorRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeLift;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

/**
 * Servo Position Finder OpMode for tuning:
 * 1. Intake Lift Servo (intakeLift)
 * 2. Depollenator Left Servo (depollenatorLeft)
 * 3. Depollenator Right Servo (depollenatorRight)
 * 4. Both Depollenator Servos simultaneously
 */
@TeleOp(name = "Servo Position Finder", group = "Testing")
public class IntakeServoTest extends OpMode {

    private Input input;

    public enum SelectedServo {
        INTAKE_LIFT,
        DEPOLLENATOR_LEFT,
        DEPOLLENATOR_RIGHT,
        DEPOLLENATOR_BOTH
    }

    private SelectedServo activeServo = SelectedServo.INTAKE_LIFT;

    private double intakeLiftPos = 0.50;
    private double depollenatorLeftPos = 0.50;
    private double depollenatorRightPos = 0.50;

    private final double[] stepSizes = {0.001, 0.005, 0.01, 0.05, 0.10};
    private int stepIndex = 2; // Default step = 0.01

    private boolean rightServoInverted = false;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        input = new Input();

        // Apply initial center positions
        applyPositions();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        // --- Select Target Servo (D-Pad Left / Right) ---
        if (input.dpad_right.down()) {
            int next = (activeServo.ordinal() + 1) % SelectedServo.values().length;
            activeServo = SelectedServo.values()[next];
        } else if (input.dpad_left.down()) {
            int prev = (activeServo.ordinal() - 1 + SelectedServo.values().length) % SelectedServo.values().length;
            activeServo = SelectedServo.values()[prev];
        }

        // --- Step Size Adjustments (Bumpers LB / RB) ---
        if (input.right_bumper.down() && stepIndex < stepSizes.length - 1) {
            stepIndex++;
        } else if (input.left_bumper.down() && stepIndex > 0) {
            stepIndex--;
        }

        double step = stepSizes[stepIndex];

        // --- Position Adjustments (D-Pad Up / Down or Right Stick Y) ---
        double delta = 0.0;
        if (input.dpad_up.down()) {
            delta = step;
        } else if (input.dpad_down.down()) {
            delta = -step;
        } else if (Math.abs(gamepad1.right_stick_y) > 0.1) {
            delta = -gamepad1.right_stick_y * 0.005; // Smooth analog joystick adjustment
        }

        // --- Position Presets (Gamepad Buttons A / X / Y) ---
        if (input.a.down()) {
            setPositionForActive(0.00);
        } else if (input.x.down()) {
            setPositionForActive(0.50);
        } else if (input.y.down()) {
            setPositionForActive(1.00);
        } else if (delta != 0.0) {
            adjustActivePosition(delta);
        }

        // Toggle Right Depollenator Servo Inversion (B Button)
        if (input.b.down()) {
            rightServoInverted = !rightServoInverted;
        }

        // --- Apply Positions to Hardware Servos ---
        applyPositions();

        // --- Driver Station Telemetry Output ---
        telemetry.addData("=== ACTIVE SELECTION ===", "");
        telemetry.addData("Selected Servo [D-Pad L/R]", activeServo);
        telemetry.addData("Step Size [LB/RB]", "%.3f", step);
        telemetry.addData("Right Servo Inverted [B]", rightServoInverted);

        telemetry.addLine();
        telemetry.addData("=== SERVO POSITIONS ===", "");
        telemetry.addData("1. Intake Lift Pos", "%.3f %s", intakeLiftPos, activeServo == SelectedServo.INTAKE_LIFT ? "<-- ACTIVE" : "");
        telemetry.addData("2. Depollenator Left Pos", "%.3f %s", depollenatorLeftPos, (activeServo == SelectedServo.DEPOLLENATOR_LEFT || activeServo == SelectedServo.DEPOLLENATOR_BOTH) ? "<-- ACTIVE" : "");
        telemetry.addData("3. Depollenator Right Pos", "%.3f %s", depollenatorRightPos, (activeServo == SelectedServo.DEPOLLENATOR_RIGHT || activeServo == SelectedServo.DEPOLLENATOR_BOTH) ? "<-- ACTIVE" : "");

        telemetry.addLine();
        telemetry.addData("=== CONTROLS ===", "");
        telemetry.addData("Adjust Position", "D-Pad Up/Down OR Right Stick Y");
        telemetry.addData("Switch Servo", "D-Pad Left / Right");
        telemetry.addData("Presets", "[A] 0.00 (Min) | [X] 0.50 (Mid) | [Y] 1.00 (Max)");
        telemetry.update();
    }

    private void adjustActivePosition(double delta) {
        switch (activeServo) {
            case INTAKE_LIFT:
                intakeLiftPos = clamp(intakeLiftPos + delta);
                break;
            case DEPOLLENATOR_LEFT:
                depollenatorLeftPos = clamp(depollenatorLeftPos + delta);
                break;
            case DEPOLLENATOR_RIGHT:
                depollenatorRightPos = clamp(depollenatorRightPos + delta);
                break;
            case DEPOLLENATOR_BOTH:
                depollenatorLeftPos = clamp(depollenatorLeftPos + delta);
                depollenatorRightPos = clamp(depollenatorRightPos + delta);
                break;
        }
    }

    private void setPositionForActive(double pos) {
        switch (activeServo) {
            case INTAKE_LIFT:
                intakeLiftPos = pos;
                break;
            case DEPOLLENATOR_LEFT:
                depollenatorLeftPos = pos;
                break;
            case DEPOLLENATOR_RIGHT:
                depollenatorRightPos = pos;
                break;
            case DEPOLLENATOR_BOTH:
                depollenatorLeftPos = pos;
                depollenatorRightPos = pos;
                break;
        }
    }

    private void applyPositions() {
        if (intakeLift != null) {
            intakeLift.setPosition(intakeLiftPos);
        }
        if (depollenatorLeft != null) {
            depollenatorLeft.setPosition(depollenatorLeftPos);
        }
        if (depollenatorRight != null) {
            double rightPos = rightServoInverted ? (1.0 - depollenatorRightPos) : depollenatorRightPos;
            depollenatorRight.setPosition(rightPos);
        }
    }

    private double clamp(double val) {
        return Math.max(0.0, Math.min(1.0, val));
    }
}
