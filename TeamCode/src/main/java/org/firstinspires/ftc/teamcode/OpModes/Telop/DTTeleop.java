package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;

import androidx.core.view.KeyEventDispatcher;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Components;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "DTTeleop")

public class DTTeleop extends OpMode {

    private Input input;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        input = new Input();

    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);


        double y= gamepad1.left_stick_y;
        double x= -gamepad1.left_stick_x;
        double rx= -gamepad1.right_stick_x;

        leftFront.setPower(y+x+rx);
        leftRear.setPower(y-x+rx);
        rightFront.setPower(y-x-rx);
        rightRear.setPower(y+x-rx);


    }
}
