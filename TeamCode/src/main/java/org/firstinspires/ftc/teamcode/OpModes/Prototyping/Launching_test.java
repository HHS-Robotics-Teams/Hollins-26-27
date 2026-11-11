package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.counterRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.LauncherStateMachine;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Launching_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.reset_Motors;

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
        if (input.cross_A.down()){
            counterRoller.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        if (input.circle_B.down()){
            counterRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        }
        if (input.left_trigger.held()) {
            intakeFront.setPower(1);
        }
        if (input.left_trigger.up()){
            intakeFront.setPower(0);
        }
        telemetry.addData("Launching Power",LaunchingPower);
        telemetry.addData("Launching Velocity", LaunchingTestVelocity);
        telemetry.addData("Counter Roller Power", CounterRollerPower);
        telemetry.addData("Counter roller direction", counterRoller.getDirection());
        telemetry.update();


    }
}
