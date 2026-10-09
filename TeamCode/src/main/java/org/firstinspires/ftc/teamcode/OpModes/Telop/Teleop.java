package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "Teleop")
public class Teleop extends OpMode {

    private Input input;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        input = new Input();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        intakeMotor.setPower(gamepad1.left_trigger);
        if (gamepad1.b) {
            intakeMotor.setPower(-0.45);
        }
    }
    }
