package org.firstinspires.ftc.teamcode.profiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.teamcode.MecanumDrive;

/**
 * theCube: goBILDA 5203 19.2:1 (312 RPM) drive motors, direct drive, no odometry pods (drive motor encoders).
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

        // TODO: tune the rest (see the Robot Configs doc for the order); record results here
        PARAMS.inPerTick = 0.02284; // ForwardPushTest 2026-10-09: (92 + 96) in / (3991.5 + 4238.75) ticks
        PARAMS.lateralInPerTick = 0.02664; // LateralPushTest 2026-10-09: 96 in / 3603.5 ticks
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
