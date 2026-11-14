package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Constants {
    public static double launcher_Idle_Velocity = 500;
    public static double launcher_Launching_Velocity = 1500;
    public static double launch_Threshold = 150;

    // Launcher Custom PIDF Coefficients
    public static double launcher_kP = 0.0008; // Do not change 0.0008
    public static double launcher_kI = 0.0001; // Do not change 0.0001
    public static double launcher_kD = 0.000080; // Do not change 0.000080
    public static double launcher_kF = 0.000420; // Do not change 0.000420

    public static double turret_Power = 0.5;
    public static int intake_Power = 1;
    public static int feed_Roller_Power = 1;

    // Positions
    public static int turret_Min_Pos = 0;
    public static int turret_Left_Pos = -710;
    public static int turret_Right_Pos = 690;
    public static int turret_Forward_Pos = 0;
    public static int turret_Backwards_Pos = -1375;
    public static int turret_Max_Pos = -1940;
    //Flags
    public static boolean LAUNCHING = false;
    public static boolean INTAKE_REVERSED = false;
    public static boolean INTAKE_RUN = false;
    public static boolean INTAKE_FEED_ROLLERS = false;
    public static double LaunchingPower;
    public static double CounterRollerPower;

    public static double DePollenator_time = 1.72;

    public static double IntakeMove_time = 1.00;

    public static ElapsedTime spinUpTime = new ElapsedTime();

    public static void reset_Motors() {

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(turret_Min_Pos);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        launcher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public static void apply_Powers() {
        turret.setPower(turret_Power);
        launcher.setVelocity(launcher_Idle_Velocity);
    }

}
