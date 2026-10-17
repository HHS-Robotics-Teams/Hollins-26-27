package org.firstinspires.ftc.teamcode.robots.nerdvana;

import org.firstinspires.ftc.teamcode.field.RouteTuning;

/** nerdvana's shape and mechanisms as routes see them. Shared with MeepMeep: no hardware imports here. */
public final class NerdRoute {
    public static final RouteTuning TUNING = new RouteTuning();

    static {
        // TODO: measure nerdvana; these are theCube's numbers
        TUNING.length = 18;
        TUNING.width = 18;
        TUNING.intakeSides = new double[] {0}; // intakeFront
        TUNING.intakeReach = 9;
        TUNING.launchDistance = 30;
        TUNING.launchFacing = 0;
        // turret: set this (e.g. 90) once NerdMechanisms.TURRET_TICKS_PER_DEGREE is measured; 0 = treat as fixed
        TUNING.launchArc = 0;
        TUNING.simLaunchSeconds = 3;   // NerdMechanisms SPIN_UP_SECONDS + FEED_SECONDS
        TUNING.simIntakeSeconds = 1.5; // NerdMechanisms INTAKE_SECONDS
    }

    private NerdRoute() {}
}
