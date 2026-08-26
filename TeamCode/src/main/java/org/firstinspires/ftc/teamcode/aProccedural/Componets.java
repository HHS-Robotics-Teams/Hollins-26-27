package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Componets {
    // Test Motor
    public static DcMotor testMotor;
    public static CRServo testCRServo;
    public static Servo testServo;


    public static void initComponents(HardwareMap hardwareMap){
        // init
        testMotor = hardwareMap.get(DcMotor.class, "testMotor");
        testCRServo = hardwareMap.get(CRServo.class, "testCRServo");
        testServo = hardwareMap.get(Servo.class, "testServo");
    }
}
