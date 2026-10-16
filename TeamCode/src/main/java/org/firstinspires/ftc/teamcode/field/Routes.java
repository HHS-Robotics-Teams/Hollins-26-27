package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Action;

/**
 * Every AUTO route, robot-independent. This is the testing list:
 *   DRAFT - MeepMeep only.
 *   TEST  - also on the Driver Station of every robot, as "~RED Hive Side [nerd]".
 * Scrimmage/competition entries are chosen per robot in robots/<robot>/<Tag>Autos.java.
 */
public enum Routes {
    HIVE_SIDE("Hive Side", Stage.TEST, AutoPaths::hiveSide);

    public enum Stage { DRAFT, TEST }

    /** The shape every route method in AutoPaths has. */
    public interface Route {
        Action build(AutoPaths.BuilderFactory drive, Alliance alliance, RouteTuning tuning, Mechanisms mechanisms);
    }

    /** Name shown on the Driver Station and in MeepMeep: 2-3 words, start + plan. */
    public final String displayName;
    public final Stage stage;
    public final Route route;

    Routes(String displayName, Stage stage, Route route) {
        this.displayName = displayName;
        this.stage = stage;
        this.route = route;
    }
}
