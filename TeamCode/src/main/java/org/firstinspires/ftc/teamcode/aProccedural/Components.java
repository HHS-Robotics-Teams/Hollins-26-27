package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Components {
    public static DcMotor frontLeftDrive;
    public static DcMotor frontRightDrive;
    public static DcMotor backLeftDrive;
    public static DcMotor backRightDrive;
    public static DcMotor Intake;
    public static CRServo Conveyor;
    public static DcMotor Launcher;
    public static CRServo Kicker;

    public static void initComponents(HardwareMap hardwareMap){
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive" );
        frontRightDrive = hardwareMap.get(DcMotor.class,"front_right_drive" );
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive" );
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive ");
        Intake = hardwareMap.get(DcMotor.class, "Intake_Motor");
        Conveyor = hardwareMap.get(CRServo.class,"Conveyor_Servo");
        Kicker = hardwareMap.get(CRServo.class,"Kicker_Servo");
        Launcher = hardwareMap.get(DcMotor.class, "Launcher_Motor");


        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Launcher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);




        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        Conveyor.setDirection(DcMotorSimple.Direction.REVERSE);
        Kicker.setDirection(DcMotorSimple.Direction.REVERSE);
        Intake.setDirection(DcMotorSimple.Direction.REVERSE);

    }
}
