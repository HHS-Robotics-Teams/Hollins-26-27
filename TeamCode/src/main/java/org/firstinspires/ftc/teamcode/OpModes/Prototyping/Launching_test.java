package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Launching_Velocity;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "Launching_test")
public class Launching_test extends OpMode {
    private Input input;
    private DcMotorEx launcher;

    @Override
    public void init() {
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        input = new Input();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        if (input.left_bumper.held()) {
            launcher.setVelocity(launcher_Launching_Velocity);
        } else {
            launcher.setVelocity(0);
        }
    }
}
