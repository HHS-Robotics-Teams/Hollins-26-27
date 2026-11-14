package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.depollenator;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeLift;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.DePollenator_time;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.IntakeMove_time;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.reset_Motors;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.spinUpTime;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Util.TeleOpDrive;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
import org.firstinspires.ftc.teamcode.aProccedural.LauncherStateMachine;
import org.firstinspires.ftc.teamcode.aProccedural.TurretStateMachine;

@TeleOp(name = "CompDriveV1")
public class CompDriveV1 extends OpMode {

    private Input input;
    private LauncherStateMachine launcherSM;
    private TurretStateMachine turretSM;

    private final ElapsedTime depollenatorTimer = new ElapsedTime();
    private double depollenatorTargetPower = 0.0;
    private boolean gp2RightBumperLast = false;
    private boolean gp2LeftBumperLast = false;

    private final ElapsedTime intakeLiftTimer = new ElapsedTime();
    private double intakeLiftTargetPower = 0.0;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        reset_Motors();

        input = new Input();
        launcherSM = new LauncherStateMachine();
        turretSM = new TurretStateMachine();

        depollenatorTimer.reset();
        intakeLiftTimer.reset();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        // Drivetrain controls
        TeleOpDrive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x * 1.1, -gamepad1.right_stick_x);

        // --- Turret Preset Positions (D-Pad via TurretStateMachine) ---
        if (input.dpad_up.down()) {
            turretSM.setState(TurretStateMachine.State.FORWARD);
        } else if (input.dpad_left.down()) {
            turretSM.setState(TurretStateMachine.State.LEFT);
        } else if (input.dpad_right.down()) {
            turretSM.setState(TurretStateMachine.State.RIGHT);
        } else if (input.dpad_down.down()) {
            turretSM.setState(TurretStateMachine.State.BACKWARD);
        }

        // --- Turret Fine Adjustments (Bumpers: bounded between min/max limit) ---
        if (input.left_bumper.held()) {
            turretSM.adjustTargetPosition(-10);
        } else if (input.right_bumper.held()) {
            turretSM.adjustTargetPosition(10);
        }

        // Keep Launcher state machine informed of active target position
        launcherSM.setTurretTargetPosition(turretSM.getTargetPosition());

        // --- Intake Lift Control (Strictly timed for IntakeMove_time) ---
        if (input.left_bumper.down()) {
            intakeLiftTargetPower = 1.0;
            intakeLiftTimer.reset();
        } else if (input.right_bumper.down()) {
            intakeLiftTargetPower = -1.0;
            intakeLiftTimer.reset();
        }

        if (intakeLift != null) {
            if (intakeLiftTimer.seconds() < IntakeMove_time) {
                intakeLift.setPower(intakeLiftTargetPower);
            } else {
                intakeLift.setPower(0.0);
            }
        }

        // --- Depollenator Control (Strictly timed for DePollenator_time) ---

        if (input.cross_A.down()) {
            depollenatorTargetPower = 1.0;
            depollenatorTimer.reset();
        } else if (input.delta_Y.down()) {
            depollenatorTargetPower = -1.0;
            depollenatorTimer.reset();
        }

        if (depollenator != null) {
            if (depollenatorTimer.seconds() < DePollenator_time) {
                depollenator.setPower(depollenatorTargetPower);
            } else {
                depollenator.setPower(0.0);
            }
        }

        // --- Toggle Intake Direction ---
        if (input.circle_B.down() || input.b.down()) {
            launcherSM.toggleIntakeReversed();
        }

        // --- Mechanism State Machine Control ---
        if (input.right_trigger.held()) {
            // Right Trigger -> Spin up flywheel first, then feed/launch
            if (!launcherSM.isSpunUp()) {
                launcherSM.spinUp();
                spinUpTime.reset();
            } else {
                launcherSM.launch();
            }
        } else if (input.left_trigger.held()) {
            // Left Trigger -> Intake mode
            launcherSM.intake();
        } else if (input.x.held() || input.square_X.held()) {
            // X Button -> Aim mode
            launcherSM.aim(turretSM.getTargetPosition());
        } else {
            // Default -> Return to Idle state
            launcherSM.idle();
        }

        // --- Update State Machine Hardware Outputs ---
        turretSM.update();
        launcherSM.update();

        // --- Driver Telemetry ---
        telemetry.addData("Launcher State", launcherSM.getState());
        telemetry.addData("Turret State", turretSM.getState());
        telemetry.addData("Spun Up", launcherSM.isSpunUp());
        telemetry.addData("Turret Target Pos", turretSM.getTargetPosition());
        telemetry.addData("Turret Current Pos", turretSM.getCurrentPosition());
        telemetry.addData("Turret Bounds", "[%d to %d]", TurretStateMachine.TURRET_MIN_LIMIT, TurretStateMachine.TURRET_MAX_LIMIT);
        telemetry.addData("Intake Lift Power", intakeLift != null ? intakeLift.getPower() : 0);
        telemetry.addData("Intake Lift Timer", "%.2fs / %.2fs", intakeLiftTimer.seconds(), IntakeMove_time);
        telemetry.addData("Depollenator Power", depollenator != null ? depollenator.getPower() : 0);
        telemetry.addData("Depollenator Timer", "%.2fs / %.2fs", depollenatorTimer.seconds(), DePollenator_time);
        telemetry.addData("Intake Reversed", launcherSM.isIntakeReversed());
        telemetry.addData("Launcher Velocity", launcher.getVelocity());
        telemetry.addData("Spin up Timer", spinUpTime.seconds());
        telemetry.update();
    }
}
