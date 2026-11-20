package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.depollenator;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeLift;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "Intake Servo Timer Test", group = "Testing")
public class IntakeServoTest extends OpMode {

    private Input input;
    private final ElapsedTime liftTimer = new ElapsedTime();
    private final ElapsedTime depollenatorTimer = new ElapsedTime();

    private double lastLiftDuration = 0.0;
    private double lastDepollenatorDuration = 0.0;
    private String lastLiftAction = "None";
    private String lastDepollenatorAction = "None";

    private boolean liftRunning = false;
    private boolean depollenatorRunning = false;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        input = new Input();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        double liftPower = 0.0;
        double depollenatorPower = 0.0;

        // --- Intake Lift CRServo Control (D-Pad Up / Down) ---
        if (input.dpad_up.held()) {
            liftPower = 1.0;
            if (!liftRunning) {
                liftRunning = true;
                liftTimer.reset();
                lastLiftAction = "UP / Raise";
            }
        } else if (input.dpad_down.held()) {
            liftPower = -1.0;
            if (!liftRunning) {
                liftRunning = true;
                liftTimer.reset();
                lastLiftAction = "DOWN / Lower";
            }
        } else {
            if (liftRunning) {
                lastLiftDuration = liftTimer.seconds();
                liftRunning = false;
            }
        }

        // --- Depollenator CRServo Control (D-Pad Right / Left) ---
        if (input.dpad_right.held()) {
            depollenatorPower = 1.0;
            if (!depollenatorRunning) {
                depollenatorRunning = true;
                depollenatorTimer.reset();
                lastDepollenatorAction = "EXTEND / Forward";
            }
        } else if (input.dpad_left.held()) {
            depollenatorPower = -1.0;
            if (!depollenatorRunning) {
                depollenatorRunning = true;
                depollenatorTimer.reset();
                lastDepollenatorAction = "RETRACT / Reverse";
            }
        } else {
            if (depollenatorRunning) {
                lastDepollenatorDuration = depollenatorTimer.seconds();
                depollenatorRunning = false;
            }
        }

        // Apply Servo Powers
        if (intakeLift != null) intakeLift.setPower(liftPower);
        if (depollenator != null) depollenator.setPower(depollenatorPower);

        // --- Telemetry Diagnostics & Timings ---
        telemetry.addData("=== INTAKE LIFT ===", "");
        telemetry.addData("Lift Power", liftPower);
        telemetry.addData("Active Run Time", liftRunning ? "%.2fs" : "0.00s", liftTimer.seconds());
        telemetry.addData("Last Action", "%s (Duration: %.2fs)", lastLiftAction, lastLiftDuration);

        telemetry.addLine();
        telemetry.addData("=== DEPOLLENATOR ===", "");
        telemetry.addData("Depollenator Power", depollenatorPower);
        telemetry.addData("Active Run Time", depollenatorRunning ? "%.2fs" : "0.00s", depollenatorTimer.seconds());
        telemetry.addData("Last Action", "%s (Duration: %.2fs)", lastDepollenatorAction, lastDepollenatorDuration);

        telemetry.addLine();
        telemetry.addData("=== CONTROLS ===", "");
        telemetry.addData("Intake Lift", "D-Pad Up (Raise) / D-Pad Down (Lower)");
        telemetry.addData("Depollenator", "D-Pad Right (Extend) / D-Pad Left (Retract)");
        telemetry.update();
    }
}
