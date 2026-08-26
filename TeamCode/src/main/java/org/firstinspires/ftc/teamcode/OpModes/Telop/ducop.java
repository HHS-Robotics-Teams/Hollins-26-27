package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Componets.testMotor;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Componets;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "ducop")
public class ducop extends OpMode {

    private Input input;

    @Override
    public void init() {
        Componets.initComponents(hardwareMap);
        input = new Input();
        testMotor.setPower(0.22);
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        if (input.a.down()){
            testMotor.setPower(0.5);
        }
        if (input.b.down()){
            testMotor.setPower(0);
        }

    }
}
