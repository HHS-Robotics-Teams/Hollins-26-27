package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.backLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.reset_Motors;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class MoveAuto extends OpMode {
    double timeAtStart;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        reset_Motors();

    }
    @Override
    public void start(){
        frontLeft.setPower(.75);
        frontRight.setPower(.75);
        backLeft.setPower(.75);
        backRight.setPower(.75);
        timeAtStart = getRuntime();
    }

    @Override
    public void loop() {
        if (getRuntime() > (timeAtStart + .5)) {
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);

        }
    }
}
