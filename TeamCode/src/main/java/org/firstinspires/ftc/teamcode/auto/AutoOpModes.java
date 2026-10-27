package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.field.Alliance;
import org.firstinspires.ftc.teamcode.field.Routes;
import org.firstinspires.ftc.teamcode.robots.Approved;
import org.firstinspires.ftc.teamcode.robots.Robot;
import org.firstinspires.ftc.teamcode.robots.RobotProfiles;

/**
 * Puts the AUTO routes on the Driver Station for the active robot:
 *   approved (robots/<robot>/<Tag>Autos.java):  "RED Hive Side", "BLUE Hive Side"
 *   TEST routes (field/Routes.java):            "~RED Hive Side [nerd]"  (hidden with a "-comp" config)
 */
public final class AutoOpModes {
    public static final String GROUP_APPROVED = "auto";
    public static final String GROUP_TEST = "auto test";

    private AutoOpModes() {}

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        Robot robot = RobotProfiles.activeOrNull();
        if (robot == null) return;

        for (Approved approved : robot.approved) {
            for (Alliance alliance : Alliance.values()) {
                manager.register(meta(alliance + " " + approved.name, GROUP_APPROVED),
                        new RouteOpMode(robot, approved.route, alliance));
            }
        }

        if (RobotProfiles.compMode()) return;

        for (Routes route : Routes.values()) {
            if (route.stage != Routes.Stage.TEST) continue;
            for (Alliance alliance : Alliance.values()) {
                manager.register(meta("~" + alliance + " " + route.displayName + " [" + robot.tag + "]", GROUP_TEST),
                        new RouteOpMode(robot, route, alliance));
            }
        }
    }

    private static OpModeMeta meta(String name, String group) {
        return new OpModeMeta.Builder()
                .setName(name)
                .setGroup(group)
                .setFlavor(OpModeMeta.Flavor.AUTONOMOUS)
                .build();
    }
}
