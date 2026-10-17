package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Example.ComponentsExample;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp (name = "Launching_test")
public class Launching_test extends OpMode {
    private Input input;

    @Override
    public void init() {
        launcher = hardwareMap.get(DcMotor.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        input = new Input();

    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if (input.left_bumper.down()) {
            launcher.setPower(1);


        }
    }
}
