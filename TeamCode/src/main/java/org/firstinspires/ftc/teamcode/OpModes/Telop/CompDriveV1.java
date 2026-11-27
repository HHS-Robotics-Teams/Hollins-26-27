package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.depollenatorLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.depollenatorRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeLift;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.depollenatorleft_high;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.depollenatorleft_low;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.depollenatorright_high;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.depollenatorright_low;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_high;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_low;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.reset_Motors;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.spinUpTime;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

    private boolean intakeLiftUp ;
    private boolean depollenatorExtended ;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        reset_Motors();

        input = new Input();
        launcherSM = new LauncherStateMachine();
        turretSM = new TurretStateMachine();
    }
    @Override
    public void start() {
        intakeLiftUp = true;
        depollenatorExtended = true;
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        // Drivetrain controls
        TeleOpDrive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x * 1, -gamepad1.right_stick_x);

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

        // Keep Launcher state machine informed of active target position
        launcherSM.setTurretTargetPosition(turretSM.getTargetPosition());

        // --- Intake Lift Toggle (Left Bumper) ---
        if (input.left_bumper.down()) {
            intakeLiftUp = !intakeLiftUp;
        }

        if (intakeLift != null) {
            intakeLift.setPosition(intakeLiftUp ? intake_high : intake_low);
        }

        // --- Depollenator Toggle (Right Bumper) ---
        if (input.right_bumper.down()) {
            depollenatorExtended = !depollenatorExtended;
        }

        if (depollenatorLeft != null) {
            depollenatorLeft.setPosition(depollenatorExtended ? depollenatorleft_high : depollenatorleft_low);
        }
        if (depollenatorRight != null) {
            depollenatorRight.setPosition(depollenatorExtended ? depollenatorright_high : depollenatorright_low);
        }

        // --- Toggle Intake Direction ---
        if (input.circle_B.down() || input.b.down()) {
            launcherSM.toggleIntakeReversed();
        }

        // --- Mechanism State Machine Control (2-Volley Launch Sequence) ---
        if (input.right_trigger.down()) {
            // Right Trigger Press -> Triggers 2-volley launch sequence
            launcherSM.startVolleySequence();
            spinUpTime.reset();
        } else if (input.left_trigger.held() && !launcherSM.isVolleySequenceActive()) {
            // Left Trigger -> Intake mode (active when not launching volleys)
            launcherSM.intake();
        } else if (!launcherSM.isVolleySequenceActive()) {
            // Default -> Return to Idle state when not launching or intaking
            launcherSM.idle();
        }

        // --- Update State Machine Hardware Outputs ---
        turretSM.update();
        launcherSM.update();

        // --- Driver Telemetry ---
        telemetry.addData("Launcher State", launcherSM.getState());
        telemetry.addData("Turret State", turretSM.getState());
        telemetry.addData("Volley Sequence Active", launcherSM.isVolleySequenceActive());
        telemetry.addData("Spun Up", launcherSM.isSpunUp());
        telemetry.addData("Turret Target Pos", turretSM.getTargetPosition());
        telemetry.addData("Turret Current Pos", turretSM.getCurrentPosition());
        telemetry.addData("Turret Bounds", "[%d to %d]", TurretStateMachine.TURRET_MIN_LIMIT, TurretStateMachine.TURRET_MAX_LIMIT);
        telemetry.addData("Intake Lift High", intakeLiftUp);
        telemetry.addData("Intake Lift Pos", intakeLift != null ? intakeLift.getPosition() : 0);
        telemetry.addData("Depollenator Extended", depollenatorExtended);
        telemetry.addData("Depollenator Left Pos", depollenatorLeft != null ? depollenatorLeft.getPosition() : 0);
        telemetry.addData("Depollenator Right Pos", depollenatorRight != null ? depollenatorRight.getPosition() : 0);
        telemetry.addData("Intake Reversed", launcherSM.isIntakeReversed());
        telemetry.addData("Launcher Velocity", launcher.getVelocity());
        telemetry.addData("Spin up Timer", spinUpTime.seconds());
        telemetry.update();
    }
}
