package org.firstinspires.ftc.teamcode.OpModes.Telop;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Autonomous(name = "chapterex1")
public class chapterex1 extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hello", "Duc");
    }

    @Override
    public void loop() {

    }
}