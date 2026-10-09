package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Components {
    public static DcMotor frontLeftWheelMotor;
    public static DcMotor frontRightWheelMotor;
    public static DcMotor backLeftWheelMotor;
    public static DcMotor backRightWheelMotor;
    public static DcMotor intakeMotor;

    public static void initComponents(HardwareMap hardwareMap) {
        frontLeftWheelMotor = hardwareMap.get(DcMotor.class, "frontLeftWheelMotor");
        frontRightWheelMotor = hardwareMap.get(DcMotor.class, "frontRightWheelMotor");
        backLeftWheelMotor = hardwareMap.get(DcMotor.class, "backLeftWheelMotor");
        backRightWheelMotor = hardwareMap.get(DcMotor.class, "backRightWheelMotor");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        frontLeftWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightWheelMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
