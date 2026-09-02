package org.firstinspires.ftc.teamcode.OpModes.Telop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp(name = "chapterex2")
public class chapterex2 extends OpMode {

    @Override
    public void init() {
        int myGrade = 88;
        double motorPower = 0.5;
        String myName = "Himler Kizma";;
        telemetry.addData("My Grade", myGrade);
        telemetry.addData("Motor Power", motorPower);
        telemetry.addData("My Name", myName);
    }

    @Override
    public void loop() {

    }
}
