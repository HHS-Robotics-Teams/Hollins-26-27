package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Action;

/**
 * The mechanism actions a route can ask for. Each robot implements this in its own folder
 * (robots/<robot>/<Tag>Mechanisms.java); MeepMeep uses timed pauses. A robot without a mechanism
 * still implements it (as a pause), so every robot can run every route.
 */
public interface Mechanisms {
    /** Launch the loaded POLLEN. aimDegrees: turret angle off RouteTuning.launchFacing; always 0 for a fixed launcher. */
    Action launch(double aimDegrees);

    /**
     * Launch at a known distance (horizontal, launcher to the CELL opening, inches). A robot with a
     * launcher speed table overrides this to pick the speed for the distance; the default ignores it.
     */
    default Action launch(double aimDegrees, double distanceIn) {
        return launch(aimDegrees);
    }

    /** Pick up POLLEN with one intake. side: index into RouteTuning.intakeSides. */
    Action intake(int side);

    /** Short description for init telemetry, e.g. "NerdMechanisms". */
    String describe();
}
