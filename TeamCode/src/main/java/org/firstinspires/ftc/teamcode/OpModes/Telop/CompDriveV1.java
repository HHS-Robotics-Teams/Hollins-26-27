package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.reset_Motors;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Backwards_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Forward_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Left_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Right_Pos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Util.TeleOpDrive;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
import org.firstinspires.ftc.teamcode.aProccedural.LauncherStateMachine;

@TeleOp(name = "CompDriveV1")
public class CompDriveV1 extends OpMode {

    private Input input;
    private LauncherStateMachine launcherSM;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        reset_Motors();

        input = new Input();
        launcherSM = new LauncherStateMachine();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        // Drivetrain controls
        TeleOpDrive.run(-gamepad1.left_stick_y, gamepad1.left_stick_x * 1.1, gamepad1.right_stick_x);

        // --- Turret Preset Positions (D-Pad) ---
        if (input.dpad_up.down()) {
            launcherSM.setTurretTargetPosition(turret_Forward_Pos);
        } else if (input.dpad_left.down()) {
            launcherSM.setTurretTargetPosition(turret_Left_Pos);
        } else if (input.dpad_right.down()) {
            launcherSM.setTurretTargetPosition(turret_Right_Pos);
        } else if (input.dpad_down.down()) {
            launcherSM.setTurretTargetPosition(turret_Backwards_Pos);
        }

        // --- Turret Fine Adjustments (Bumpers) ---
        if (input.left_bumper.held()) {
            launcherSM.setTurretTargetPosition(launcherSM.getTurretTargetPosition() - 10);
        } else if (input.right_bumper.held()) {
            launcherSM.setTurretTargetPosition(launcherSM.getTurretTargetPosition() + 10);
        }

        // --- Toggle Intake Direction ---
        if (input.circle.down() || input.b.down()) {
            launcherSM.toggleIntakeReversed();
        }

        // --- Mechanism State Machine Control ---
        if (input.right_trigger.held()) {
            // Right Trigger -> Spin up flywheel first, then feed/launch
            if (!launcherSM.isSpunUp()) {
                launcherSM.spinUp();
            } else {
                launcherSM.launch();
            }
        } else if (input.left_trigger.held()) {
            // Left Trigger -> Intake mode
            launcherSM.intake();
        } else if (input.x.held()) {
            // X Button -> Aim mode
            launcherSM.aim(launcherSM.getTurretTargetPosition());
        } else {
            // Default -> Return to Idle state
            launcherSM.idle();
        }

        // --- Update State Machine Hardware Outputs ---
        launcherSM.update();

        // --- Driver Telemetry ---
        telemetry.addData("State", launcherSM.getState());
        telemetry.addData("Spun Up", launcherSM.isSpunUp());
        telemetry.addData("Turret Target Pos", launcherSM.getTurretTargetPosition());
        telemetry.addData("Turret Current Pos", turret.getCurrentPosition());
        telemetry.addData("Intake Reversed", launcherSM.isIntakeReversed());
        telemetry.update();
    }
}
