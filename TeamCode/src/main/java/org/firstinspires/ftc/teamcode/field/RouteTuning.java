package org.firstinspires.ftc.teamcode.field;

/**
 * What a route needs to know about a robot's shape and mechanisms, so any route runs on any robot.
 * Each robot fills one in (robots/<robot>/<Tag>Route.java); MeepMeep reads the same file.
 * Angles are degrees measured from the robot's front, counterclockwise positive (90 = left side).
 */
public class RouteTuning {
    // robot footprint, bumper to bumper (in)
    public double length = 18; // front to back
    public double width = 18;  // side to side

    // intake: which sides can pick up POLLEN, and how far the pickup point is from the robot center
    public double[] intakeSides = {0};
    public double intakeReach = 9; // in

    // launcher
    public double launchDistance = 30; // in from the CELL where this launcher scores
    public double launchFacing = 0;    // direction the launcher shoots when centered
    public double launchArc = 0;       // turret: how far it can aim either side of launchFacing; 0 = fixed launcher

    // MeepMeep only: how long the mechanism actions take, so simulated AUTO time stays realistic
    public double simLaunchSeconds = 3;
    public double simIntakeSeconds = 1.5;
}
