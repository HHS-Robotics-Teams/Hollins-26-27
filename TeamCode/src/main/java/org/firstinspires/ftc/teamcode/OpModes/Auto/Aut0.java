package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.backLeftDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backRightDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontLeftDrive;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontRightDrive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class Aut0 extends OpMode {
    double timeAtStart;
    @Override
    public void init() {
        Components.initComponents(hardwareMap);


    }
    @Override
    public void start(){
        timeAtStart=getRuntime();
        frontLeftDrive.setPower(0.2);
        frontRightDrive.setPower(0.2);
        backLeftDrive.setPower(0.2);
        backRightDrive.setPower(0.2);
    }
    @Override
    public void loop() {
        if (getRuntime() > (timeAtStart+1)) {
            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);
        }



    }
}
