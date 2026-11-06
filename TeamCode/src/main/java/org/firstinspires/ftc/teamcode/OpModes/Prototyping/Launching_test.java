package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.counterRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Launching_Velocity;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "Launching_test")
public class Launching_test extends OpMode {
    private Input input;
    private double LaunchingTestVelocity;
    private double LaunchingPower;
    private double CounterRollerPower;



    @Override
    public void init() {
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);

        counterRoller = hardwareMap.get(CRServo.class, "counterRoller");
        counterRoller.setDirection(DcMotorSimple.Direction.FORWARD);

        input = new Input();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        LaunchingPower = launcher.getPower();
        CounterRollerPower = (LaunchingPower * .95);

        if (input.dpad_up.down()) {
            LaunchingTestVelocity = (LaunchingTestVelocity + 100);

        }
        if (input.dpad_down.down()){
            LaunchingTestVelocity = (LaunchingTestVelocity - 100);
        }

        if (input.left_bumper.held()) {
            launcher.setVelocity(LaunchingTestVelocity);
            counterRoller.setPower(CounterRollerPower);
        } else {
            launcher.setVelocity(0);
            counterRoller.setPower(0);
        }
        if (input.a.down()){
            counterRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        if (input.b.down()){
            counterRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        }
        telemetry.addData("Launching Power",LaunchingPower);
        telemetry.addData("Launching Velocity", LaunchingTestVelocity);
        telemetry.addData("Counter Roller Power", CounterRollerPower);
        telemetry.addData("Counter roller direction", counterRoller.getDirection());
        telemetry.update();


    }
}
