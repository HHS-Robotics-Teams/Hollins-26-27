package org.firstinspires.ftc.teamcode.robots.nerdvana;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.field.RouteTuning;
import org.firstinspires.ftc.teamcode.robots.Robot;

/**
 * nerdvana (15006): goBILDA Pinpoint with 2 odometry pods. Drive motor type not recorded yet.
 * Robot config name on the Control Hub: "nerdvana" (or "nerdvana-anything").
 */
@Config
public final class Nerdvana {
    public static final String CONFIG_NAME = "nerdvana";
    public static final String TAG = "nerd";

    public static MecanumDrive.Params PARAMS = new MecanumDrive.Params();

    // route geometry (shared with MeepMeep in NerdRoute); tune live in Dashboard under Nerdvana.ROUTE
    public static RouteTuning ROUTE = NerdRoute.TUNING;

    static {
        PARAMS.usePinpoint = true;

        // right side reversed, as in the team's Components.java; confirm with MecanumMotorDirectionDebugger
        PARAMS.reverseRightFront = true;
        PARAMS.reverseRightBack = true;

        PARAMS.logoFacingDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
        PARAMS.usbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;

        // TODO: tune the rest (see the Robot Configs doc for the order); record results here
        PARAMS.inPerTick = 0.001978956; // starting value for goBILDA 4-bar pods (19.894 ticks/mm); confirm with ForwardPushTest
        PARAMS.lateralInPerTick = PARAMS.inPerTick;
        PARAMS.trackWidthTicks = 0;

        PARAMS.pinpointParYTicks = 0.0; // from AngularRampLogger
        PARAMS.pinpointPerpXTicks = 0.0; // from AngularRampLogger
        PARAMS.reversePinpointPar = false; // DeadWheelDirectionDebugger
        PARAMS.reversePinpointPerp = false; // DeadWheelDirectionDebugger

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

    public static final Robot ROBOT = new Robot(CONFIG_NAME, TAG, PARAMS, ROUTE, NerdMechanisms::new, NerdAutos.APPROVED);

    private Nerdvana() {}
}
