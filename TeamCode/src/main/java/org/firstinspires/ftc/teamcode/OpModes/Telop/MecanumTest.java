package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.Conveyor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.Intake;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.Kicker;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.Launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backLeftDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backRightDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontLeftDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontRightDrive;






import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
//code = make robot work
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "hi ", group = "Quantum bots")
public class MecanumTest extends OpMode {
    private Input input;
    boolean motorOn = false;
    boolean lastA = false;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
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
        //Launcher.setPower(.75);


        //if (input.y.down()) {
        //    frontLeftDrive.setPower(.5);
        //}
        //if (input.x.down()) {
        //    backLeftDrive.setPower(.5);
        //}


            if (input.left_trigger.down()) {
            Intake.setPower(.5);
        }
        if (input.left_trigger.up()) {
            Intake.setPower(.0);
        }
//AO ONI was here.....
        if (input.right_trigger.down()) {
            Conveyor.setPower(1);
            Kicker.setPower(1);
        }
        if (input.right_trigger.up()) {
            Conveyor.setPower(0);
            Kicker.setPower(0);
        }


        if (gamepad1.a && !lastA) {
            motorOn = !motorOn;
        }
        if (motorOn) {
            Launcher.setPower(.465);
        } else {
            Launcher.setPower(.0);
        }
        lastA = gamepad1.a;


    }
}