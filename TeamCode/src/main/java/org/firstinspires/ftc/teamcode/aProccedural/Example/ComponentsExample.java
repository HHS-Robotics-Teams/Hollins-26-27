package org.firstinspires.ftc.teamcode.aProccedural.Example;



import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Util.AprilTagHelper;

/**
 * File to store all hardware code
 */
public class ComponentsExample {

    //Instantiate Drive Motors
    public static DcMotor leftFront;
    public static DcMotor rightFront;
    public static DcMotor leftBack;
    public static DcMotor rightBack;

    public static DcMotor arm_tilt;
    public static DcMotor claw_tilt;

    public static Servo pincer_left;

    public static AprilTagHelper tagHelper;


    /*
        Method to initialize components
        param hardwareMap is hardwareMap
     */
    public static void initComponents(HardwareMap hardwareMap){

        //Initialize Drive Motors
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // Initialize Arm Motors
        claw_tilt = hardwareMap.get(DcMotor.class,"ClawTilt");
        arm_tilt = hardwareMap.get(DcMotor.class,"ArmTilt");

        //Initialize Servos
        pincer_left = hardwareMap.get(Servo.class,"PincerLeft");

        //Drive Motor Settings
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Arm Motor Settings
        claw_tilt.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm_tilt.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }


}
