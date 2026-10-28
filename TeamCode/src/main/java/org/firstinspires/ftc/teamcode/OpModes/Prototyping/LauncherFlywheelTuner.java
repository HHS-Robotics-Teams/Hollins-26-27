package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Idle_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Launching_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kF;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kI;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kP;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.reset_Motors;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
import org.firstinspires.ftc.teamcode.aProccedural.PIDFController;

/**
 * Interactive Live Tuner for the Launcher Flywheel Custom PIDF Loop.
 * Allows live gamepad tuning of kP, kI, kD, kF, and Target Velocity on the Driver Station.
 */
@TeleOp(name = "Launcher Flywheel Tuner", group = "Tuning")
public class LauncherFlywheelTuner extends OpMode {

    // Tuneable parameters
    public double tuning_kP = 0.0008;
    public double tuning_kI = 0.0001;
    public double tuning_kD = 0.00005;
    public double tuning_kF = 0.00045; // ~1 / maxVelocity
    public double tuning_TargetVelocity = 1000;

    private Input input;
    private PIDFController pidf;
    private boolean motorEnabled = false;

    // Gamepad tuning helper state
    public enum Parameter {
        KP, KI, KD, KF, TARGET_VELOCITY
    }
    private Parameter selectedParam = Parameter.KP;
    private final double[] stepSizes = {0.00001, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0};
    private int stepIndex = 1; // Default step = 0.0001

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        reset_Motors();

        input = new Input();

        // Pull initial values from Constants
        tuning_kP = launcher_kP;
        tuning_kI = launcher_kI;
        tuning_kD = launcher_kD;
        tuning_kF = launcher_kF;
        tuning_TargetVelocity = launcher_Launching_Velocity;

        pidf = new PIDFController(tuning_kP, tuning_kI, tuning_kD, tuning_kF);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        // --- Gamepad Interactive Controls ---

        // Toggle Motor ON/OFF (Step Response Test)
        if (input.a.down()) {
            motorEnabled = !motorEnabled;
            if (!motorEnabled) {
                pidf.reset();
            }
        }

        // Switch Target Velocity Preset (Idle vs Launching)
        if (input.b.down()) {
            if (tuning_TargetVelocity == launcher_Launching_Velocity) {
                tuning_TargetVelocity = launcher_Idle_Velocity;
            } else {
                tuning_TargetVelocity = launcher_Launching_Velocity;
            }
        }

        // Reset PIDF Integral Sum & History
        if (input.x.down()) {
            pidf.reset();
        }

        // Save Current Tuned Values into Constants memory
        if (input.y.down()) {
            launcher_kP = tuning_kP;
            launcher_kI = tuning_kI;
            launcher_kD = tuning_kD;
            launcher_kF = tuning_kF;
            launcher_Launching_Velocity = tuning_TargetVelocity;
        }

        // Cycle Selected Parameter (D-Pad Left/Right)
        if (input.dpad_right.down()) {
            int next = (selectedParam.ordinal() + 1) % Parameter.values().length;
            selectedParam = Parameter.values()[next];
        } else if (input.dpad_left.down()) {
            int prev = (selectedParam.ordinal() - 1 + Parameter.values().length) % Parameter.values().length;
            selectedParam = Parameter.values()[prev];
        }

        // Adjust Step Size (Left / Right Bumpers)
        if (input.right_bumper.down() && stepIndex < stepSizes.length - 1) {
            stepIndex++;
        } else if (input.left_bumper.down() && stepIndex > 0) {
            stepIndex--;
        }

        // Adjust Parameter Value Up / Down (D-Pad Up/Down)
        double currentStep = stepSizes[stepIndex];
        if (input.dpad_up.down()) {
            adjustParameter(selectedParam, currentStep);
        } else if (input.dpad_down.down()) {
            adjustParameter(selectedParam, -currentStep);
        }

        // --- Execute Custom PIDF Loop ---
        pidf.setPIDF(tuning_kP, tuning_kI, tuning_kD, tuning_kF);

        double currentVelocity = launcher.getVelocity();
        double target = motorEnabled ? tuning_TargetVelocity : 0;
        double power = motorEnabled ? pidf.calculate(target, currentVelocity) : 0;

        launcher.setPower(power);

        // --- Driver Station Telemetry Output ---
        double error = target - currentVelocity;

        telemetry.addData("01. Motor State", motorEnabled ? "RUNNING [Press A to STOP]" : "STOPPED [Press A to START]");
        telemetry.addData("02. Target Velocity", "%.1f ticks/s [Press B to switch preset]", target);
        telemetry.addData("03. Actual Velocity", "%.1f ticks/s", currentVelocity);
        telemetry.addData("04. Error", "%.1f ticks/s", error);
        telemetry.addData("05. Calculated Power", "%.3f", power);

        telemetry.addData("-- PIDF Outputs --", "");
        telemetry.addData("P Output", "%.4f", pidf.getP());
        telemetry.addData("I Output", "%.4f", pidf.getI());
        telemetry.addData("D Output", "%.4f", pidf.getD());
        telemetry.addData("F Output", "%.4f", pidf.getF());

        telemetry.addData("-- Gamepad Tuning Controls --", "");
        telemetry.addData("Selected Param [D-Pad L/R]", selectedParam);
        telemetry.addData("Step Size [LB/RB]", currentStep);
        telemetry.addData("kP", "%.6f", tuning_kP);
        telemetry.addData("kI", "%.6f", tuning_kI);
        telemetry.addData("kD", "%.6f", tuning_kD);
        telemetry.addData("kF", "%.6f", tuning_kF);
        telemetry.addData("Target Vel", "%.1f", tuning_TargetVelocity);
        telemetry.addData("Action Hints", "[Y] Save to Constants | [X] Reset PIDF");

        telemetry.update();
    }

    private void adjustParameter(Parameter param, double delta) {
        switch (param) {
            case KP:
                tuning_kP = Math.max(0, tuning_kP + delta);
                break;
            case KI:
                tuning_kI = Math.max(0, tuning_kI + delta);
                break;
            case KD:
                tuning_kD = Math.max(0, tuning_kD + delta);
                break;
            case KF:
                tuning_kF = Math.max(0, tuning_kF + delta);
                break;
            case TARGET_VELOCITY:
                tuning_TargetVelocity = Math.max(0, tuning_TargetVelocity + delta);
                break;
        }
    }
}
