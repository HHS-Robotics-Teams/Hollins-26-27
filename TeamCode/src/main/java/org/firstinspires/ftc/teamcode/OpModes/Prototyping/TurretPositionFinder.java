package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Constants;
import org.firstinspires.ftc.teamcode.aProccedural.Input;
import org.firstinspires.ftc.teamcode.aProccedural.TurretStateMachine;

@TeleOp(name = "Position Finder", group = "Testing")
public class TurretPositionFinder extends OpMode {

    private Input input;
    private TurretStateMachine turretSM;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        Constants.reset_Motors();

        input = new Input();
        turretSM = new TurretStateMachine();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        // --- Preset Positions (D-Pad) ---
        if (input.dpad_up.down()) {
            turretSM.setState(TurretStateMachine.State.FORWARD);
        } else if (input.dpad_left.down()) {
            turretSM.setState(TurretStateMachine.State.LEFT);
        } else if (input.dpad_right.down()) {
            turretSM.setState(TurretStateMachine.State.RIGHT);
        } else if (input.dpad_down.down()) {
            turretSM.setState(TurretStateMachine.State.BACKWARD);
        }

        // --- Fine Adjustments (Bumpers: +/-5 ticks per loop tick held) ---
        if (input.right_bumper.held()) {
            turretSM.adjustTargetPosition(5);
        } else if (input.left_bumper.held()) {
            turretSM.adjustTargetPosition(-5);
        }

        // --- Coarse Adjustments (Triggers: +/-25 ticks per press) ---
        if (input.right_trigger.down()) {
            turretSM.adjustTargetPosition(25);
        } else if (input.left_trigger.down()) {
            turretSM.adjustTargetPosition(-25);
        }

        // --- Reset Encoder Zero Position (A / Cross button) ---
        if (input.cross_A.down() || input.a.down()) {
            turretSM.resetEncoder();
        }

        // --- Update Hardware ---
        turretSM.update();

        // --- Telemetry ---
        telemetry.addData("Turret State", turretSM.getState());
        telemetry.addData("Current Position", turretSM.getCurrentPosition());
        telemetry.addData("Target Position", turretSM.getTargetPosition());
        telemetry.addData("Position Error", turretSM.getPositionError());
        telemetry.addData("At Target", turretSM.isAtTarget());
        telemetry.addLine();
        telemetry.addData("=== CONTROLS ===", "");
        telemetry.addData("Presets", "D-Pad Up (Forward), Left, Right, Down (Backward)");
        telemetry.addData("Fine Adjust (+/-5)", "Bumpers (RB / LB)");
        telemetry.addData("Coarse Adjust (+/-25)", "Triggers (RT / LT)");
        telemetry.addData("Reset Encoder", "A / Cross");
        telemetry.update();
    }
}
