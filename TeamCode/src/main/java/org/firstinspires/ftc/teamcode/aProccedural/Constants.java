package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Constants {
    public static double launcher_Idle_Velocity = 500;
    public static double launcher_Launching_Velocity = 1000;

    // Launcher Custom PIDF Coefficients
    public static double launcher_kP = 0.0008;
    public static double launcher_kI = 0.0001;
    public static double launcher_kD = 0.00005;
    public static double launcher_kF = 0.00045;

    public static double turret_Power = 0.5;
    public static int intake_Power = 1;
    public static int feed_Roller_Power = 1;

    // Positions
    public static int turret_Min_Pos = 0;
    public static int turret_Left_Pos = -500;
    public static int turret_Right_Pos = 500;
    public static int turret_Forward_Pos = 0;
    public static int turret_Backwards_Pos = 1000;
    //Flags
    public static boolean LAUNCHING = false;
    public static boolean INTAKE_REVERSED = false;
    public static boolean INTAKE_RUN = false;
    public static boolean INTAKE_FEED_ROLLERS = false;

    public static void reset_Motors() {
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setTargetPosition(turret_Min_Pos);

        launcher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public static void apply_Powers() {
        turret.setPower(turret_Power);
        launcher.setVelocity(launcher_Idle_Velocity);
    }
}
