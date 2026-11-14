package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.robots.Robot;
import org.firstinspires.ftc.teamcode.robots.RobotProfiles;

/**
 * Puts the TELEOP drive on the Driver Station for the active robot, e.g. "Drive [cube]".
 * Nothing is shown if the config name matches no robot (see RobotProfiles).
 */
public final class TeleOpModes {
    public static final String GROUP = "teleop";

    private TeleOpModes() {}

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        Robot robot = RobotProfiles.activeOrNull();
        if (robot == null) return;

        manager.register(new OpModeMeta.Builder()
                        .setName("Drive [" + robot.tag + "]")
                        .setGroup(GROUP)
                        .setFlavor(OpModeMeta.Flavor.TELEOP)
                        .build(),
                new DriveOpMode(robot));
    }
}
