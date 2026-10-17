package org.firstinspires.ftc.teamcode.robots.thecube;

import org.firstinspires.ftc.teamcode.field.RouteTuning;

/** theCube's shape and mechanisms as routes see them. Shared with MeepMeep: no hardware imports here. */
public final class CubeRoute {
    public static final RouteTuning TUNING = new RouteTuning();

    static {
        TUNING.length = 18;
        TUNING.width = 18;
        // no intake or launcher yet: CubeMechanisms pauses instead, so these just shape the path
        TUNING.intakeSides = new double[] {0};
        TUNING.intakeReach = 9;
        TUNING.launchDistance = 30;
        TUNING.launchFacing = 0;
        TUNING.launchArc = 0;
    }

    private CubeRoute() {}
}
