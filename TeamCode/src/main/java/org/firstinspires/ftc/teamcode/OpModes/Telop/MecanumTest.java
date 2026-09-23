package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Componets.backLeftDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Componets.backRightDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Componets.frontLeftDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Componets.frontRightDrive;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.aProccedural.Componets;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp
public class MecanumTest extends OpMode {
    private Input input;

    @Override
    public void init() {
        Componets.initComponents(hardwareMap);
        input = new Input();




    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);
        double y = -gamepad1.left_stick_y; // Remember, Y stick is reversed!
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x; // Jakson was here!

        frontLeftDrive.setPower(y + x + rx);
        backLeftDrive.setPower(y - x + rx);
        frontRightDrive.setPower(y - x - rx);
        backRightDrive.setPower(y + x - rx);
    }
}