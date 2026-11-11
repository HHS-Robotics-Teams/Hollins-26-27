package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.spindexer;

import androidx.core.view.KeyEventDispatcher;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.internal.camera.delegating.DelegatingCaptureSequence;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "DO NOT USE - DT")
public class Drivetrain extends OpMode {

    // Setup
    private Input input;

    @Override
    public void init() {
        // Setup
        Components.initComponents(hardwareMap);
        input = new Input();
    }

    @Override
    public void loop() {
        // Setup
        input.pollGamepad(gamepad1);

        // Drivetrain
        loopIt();

    }

    public void loopIt(){
        double y= gamepad1.left_stick_y;
        double x= -gamepad1.left_stick_x;
        double rx= -gamepad1.right_stick_x;

        leftFront.setPower(y+x+rx);
        leftRear.setPower(y-x+rx);
        rightFront.setPower(y-x-rx);
        rightRear.setPower(y+x-rx);
    }
}