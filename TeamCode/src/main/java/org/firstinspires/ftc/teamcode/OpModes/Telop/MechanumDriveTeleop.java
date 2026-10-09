package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcherMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "Teleop")
public class MechanumDriveTeleop extends OpMode {
    private Input input;
    Components drive = new Components();

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        input = new Input();
    }

    @Override
    public void loop() {
        double forward = -gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;
        drive.drive(forward, right, rotate);
        input.pollGamepad(gamepad1);
        if (input.left_trigger.down()) {
            intakeMotor.setPower(gamepad1.left_trigger);
        }
        if (input.right_trigger.down()) {
            launcherMotor.setPower(gamepad1.right_trigger);
        }
        if (input.b.down()) {
            intakeMotor.setPower(-0.45);
        }
    }
}
