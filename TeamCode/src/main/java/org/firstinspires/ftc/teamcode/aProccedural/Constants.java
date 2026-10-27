package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.kicker;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;

public class Constants {
    public static double launcher_Power = 0.5;
    public static double launching_Power = 1;
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
    public static Boolean LAUNCHING;
    public static Boolean INTAKE_REVERSED;
    public static Boolean INTAKE_RUN;
    public static Boolean INTAKE_FEED_ROLLERS;

    public static Void apply_Powers() {
        leftFeedRoller.setPower(feed_Roller_Power);
        rightFeedRoller.setPower(feed_Roller_Power);
        kicker.setPower(feed_Roller_Power);
        intakeFront.setPower(intake_Power);
        turret.setPower(turret_Power);
        launcher.setPower(launcher_Power);
        return null;
    }
}
