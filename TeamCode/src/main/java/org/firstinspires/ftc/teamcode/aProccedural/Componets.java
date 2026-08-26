package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Componets {
    // Test Motor
    public static DcMotor testMotor;


    public static void initComponents(HardwareMap hardwareMap){
        // init
        testMotor = hardwareMap.get(DcMotor.class, "testMotor");
    }
}
