package org.firstinspires.ftc.teamcode.OpModes.Telop;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "chapterex4")
public class chapterex4 extends OpMode {

    private Input input;
    @Override
    public void init() {
        input = new Input();
    }

    @Override
    public void loop() {
        boolean aButton = gamepad1.a;
        double yLevel = -gamepad1.left_stick_y;
        double xLevel = -gamepad1.left_stick_x;

        if (yLevel < 0) {
            telemetry.addData("Left Stick Y", "Negative");
        } else if (yLevel > 0) {
            telemetry.addData("Left Stick Y", "Positive");
        }
        // Turbo Button
        if (aButton) {
            telemetry.addData("A Button", "pressed");
            telemetry.addData("Forward Speed", gamepad1.left_stick_y);
        } else if (!aButton) {
            telemetry.addData("A Button", "not pressed");
            telemetry.addData("Forward Speed", gamepad1.left_stick_y * 0.5);
        }
    }
}
