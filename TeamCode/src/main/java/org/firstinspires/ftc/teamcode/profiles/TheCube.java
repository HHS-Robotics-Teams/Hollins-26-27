package org.firstinspires.ftc.teamcode.profiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.teamcode.MecanumDrive;

/**
 * theCube: goBILDA 5203 1:1 drive motors, no odometry pods (drive motor encoders).
 * Robot config name on the Control Hub: "theCube" (or "theCube-anything").
 */
@Config
public final class TheCube {
    public static final String CONFIG_NAME = "theCube";

    public static MecanumDrive.Params PARAMS = new MecanumDrive.Params();

    static {
        PARAMS.usePinpoint = false;

        PARAMS.reverseLeftFront = true;
        PARAMS.reverseLeftBack = true;

        PARAMS.logoFacingDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        PARAMS.usbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;

        // TODO: tune (see the Robot Configs doc for the order); record results here
        //   5203 1:1 = 28 ticks per wheel rev, so inPerTick ~= pi * wheelDiameterIn / 28
        PARAMS.inPerTick = 1;
        PARAMS.lateralInPerTick = 1;
        PARAMS.trackWidthTicks = 0;

        PARAMS.kS = 0;
        PARAMS.kV = 0;
        PARAMS.kA = 0;

        PARAMS.axialGain = 0.0;
        PARAMS.lateralGain = 0.0;
        PARAMS.headingGain = 0.0;

        PARAMS.axialVelGain = 0.0;
        PARAMS.lateralVelGain = 0.0;
        PARAMS.headingVelGain = 0.0;
    }

    private TheCube() {}
}
