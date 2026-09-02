package org.firstinspires.ftc.teamcode.OpModes.Telop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "chapterex3")
public class chapterex3 extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {
        double ySpeed = -gamepad1.left_stick_y / 2;
        double ySpeedRight = -gamepad1.right_stick_y / 2;
        double xSpeed = gamepad1.left_stick_x / 2;
        double turnSpeed = gamepad1.right_stick_x;
        boolean bButton = gamepad1.b;
        double triggerSum = gamepad1.left_trigger + gamepad1.right_trigger;
        double y_stick_diff = Math.abs(ySpeed - ySpeedRight);
        telemetry.addData("Y Speed", ySpeed);
        telemetry.addData("X Speed", xSpeed);
        telemetry.addData("Turn Speed", turnSpeed);
        telemetry.addData("B Button", bButton);
        telemetry.addData("Trigger Sum", triggerSum);
    }
}
