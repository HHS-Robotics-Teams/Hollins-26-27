package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.backLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontRight;


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

        frontLeft.setPower(y+x+rx);
        backLeft.setPower(y-x+rx);
        frontRight.setPower(y-x-rx);
        backRight.setPower(y+x-rx);


    }
}
