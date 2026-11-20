package org.firstinspires.ftc.teamcode.Auto;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.OpModes.Telop.spindexerTest;
import org.firstinspires.ftc.teamcode.aProccedural.Components;

@Autonomous
public class M1_Auto extends OpMode {
    double timeAtStart;

    public void init() {
        Components.initComponents(hardwareMap);
    }

    public void start(){
        //spindexerTest.spinTheDexerLikeCrazy();
        leftFront.setPower(0.75);
        leftRear.setPower(0.75);
        rightFront.setPower(0.75);
        rightRear.setPower(0.75);
        timeAtStart = getRuntime();
    }
    public void loop(){
        if (getRuntime() > (timeAtStart + .5)){
            leftFront.setPower(0);
            leftRear.setPower(0);
            rightFront.setPower(0);
            rightRear.setPower(0);
        }
    }
}

