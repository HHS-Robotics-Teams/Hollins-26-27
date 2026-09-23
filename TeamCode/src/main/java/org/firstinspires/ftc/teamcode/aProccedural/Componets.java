package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Componets {
    public static DcMotor frontLeftDrive;
    public static DcMotor frontRightDrive;
    public static DcMotor backLeftDrive;
    public static DcMotor backRightDrive;

    public static void initComponents(HardwareMap hardwareMap){
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive" );
        frontRightDrive = hardwareMap.get(DcMotor.class,"front_right_drive" );
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive" );
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive ");


        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
