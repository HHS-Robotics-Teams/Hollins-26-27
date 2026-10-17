package org.firstinspires.ftc.teamcode.robots;

import com.qualcomm.ftccommon.configuration.RobotConfigFile;
import com.qualcomm.ftccommon.configuration.RobotConfigFileManager;

import org.firstinspires.ftc.teamcode.robots.nerdvana.Nerdvana;
import org.firstinspires.ftc.teamcode.robots.thecube.TheCube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Picks the robot from the active robot config name. The part before the first '-' selects the
 * robot (case-insensitive), so "theCube" and "theCube-noIntake" both load TheCube.
 * A "-comp" part ("nerdvana-comp") switches AUTO to the approved list only (scrimmage mode).
 *
 * To add a robot: copy a robots/<robot>/ folder, rename its classes with the new tag, and add it below.
 */
public final class RobotProfiles {
    private static final List<Robot> ROBOTS = Arrays.asList(TheCube.ROBOT, Nerdvana.ROBOT);

    private RobotProfiles() {}

    public static String activeConfigName() {
        RobotConfigFile config = new RobotConfigFileManager().getActiveConfig();
        return config.isNoConfig() ? "" : config.getName();
    }

    /** The active robot, or null if the config name matches none. */
    public static Robot activeOrNull() {
        String prefix = activeConfigName().split("-", 2)[0];
        for (Robot robot : ROBOTS) {
            if (robot.configName.equalsIgnoreCase(prefix)) {
                return robot;
            }
        }
        return null;
    }

    public static Robot forActiveConfig() {
        Robot robot = activeOrNull();
        if (robot == null) {
            List<String> names = new ArrayList<>();
            for (Robot r : ROBOTS) names.add(r.configName);
            throw new RuntimeException("Unknown robot config \"" + activeConfigName()
                    + "\": name it one of " + names + " (optionally with -suffix)");
        }
        return robot;
    }

    /** True when the config name has a "-comp" part: show only the approved AUTO list. */
    public static boolean compMode() {
        String[] parts = activeConfigName().split("-");
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase("comp")) return true;
        }
        return false;
    }
}
