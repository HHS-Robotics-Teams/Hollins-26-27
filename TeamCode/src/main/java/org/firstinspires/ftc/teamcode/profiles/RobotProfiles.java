package org.firstinspires.ftc.teamcode.profiles;

import com.qualcomm.ftccommon.configuration.RobotConfigFile;
import com.qualcomm.ftccommon.configuration.RobotConfigFileManager;

import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Picks a robot's drive params from the active robot config name.
 * The part of the config name before the first '-' selects the profile (case-insensitive),
 * so "theCube" and "theCube-noIntake" both load TheCube.
 *
 * To add a robot: copy TheCube.java, rename it, and add one line below.
 */
public final class RobotProfiles {
    private static final Map<String, MecanumDrive.Params> PROFILES = new LinkedHashMap<>();

    static {
        PROFILES.put(TheCube.CONFIG_NAME, TheCube.PARAMS);
    }

    private RobotProfiles() {}

    public static String activeConfigName() {
        RobotConfigFile config = new RobotConfigFileManager().getActiveConfig();
        return config.isNoConfig() ? "" : config.getName();
    }

    public static MecanumDrive.Params forActiveConfig() {
        String configName = activeConfigName();
        String prefix = configName.split("-", 2)[0];
        for (Map.Entry<String, MecanumDrive.Params> e : PROFILES.entrySet()) {
            if (e.getKey().equalsIgnoreCase(prefix)) {
                return e.getValue();
            }
        }
        throw new RuntimeException("Unknown robot config \"" + configName
                + "\": name it one of " + PROFILES.keySet() + " (optionally with -suffix)");
    }
}
