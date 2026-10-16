package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

/**
 * Turns "what to do" into "where the robot stands" for a given robot's RouteTuning, so routes never
 * hard-code robot headings. Routes say: start against this wall, launch at this CELL, pick up POLLEN
 * here coming from this direction. These helpers pick the heading, the intake side and the turret angle.
 *
 * All angles here are radians in the field frame (see FieldPositions); work in the red frame and
 * mirror the results with Alliance.pose().
 */
public final class Approach {
    private Approach() {}

    /** Where the robot stops to pick up, and which intake side to run (index into intakeSides). */
    public static final class Pickup {
        public final Pose2d pose;
        public final int side;

        Pickup(Pose2d pose, int side) {
            this.pose = pose;
            this.side = side;
        }
    }

    /** Where the robot stops to launch, and the turret angle in degrees (0 for a fixed launcher). */
    public static final class Launch {
        public final Pose2d pose;
        public final double aimDegrees;

        Launch(Pose2d pose, double aimDegrees) {
            this.pose = pose;
            this.aimDegrees = aimDegrees;
        }
    }

    /**
     * Robot pose touching a wall. wallContact: point on the wall; intoField: direction from the wall
     * into the field. Works for any heading: the robot's extent toward the wall depends on its size.
     */
    public static Pose2d againstWall(Vector2d wallContact, double intoField, double heading, RouteTuning t) {
        double rel = heading - intoField;
        double half = Math.abs(Math.cos(rel)) * t.length / 2 + Math.abs(Math.sin(rel)) * t.width / 2;
        return new Pose2d(wallContact.plus(unit(intoField).times(half)), heading);
    }

    /** Heading that points the launcher (turret centered) at target. */
    public static double aimHeading(Vector2d from, Vector2d target, RouteTuning t) {
        return FieldPositions.headingTo(from, target) - Math.toRadians(t.launchFacing);
    }

    /** The spot launchDistance from target, in direction fromTarget (radians). */
    public static Vector2d launchSpot(Vector2d target, double fromTarget, RouteTuning t) {
        return target.plus(unit(fromTarget).times(t.launchDistance));
    }

    /**
     * Launch at target from spot. A turret robot keeps its current heading when the turret can reach;
     * otherwise (or with a fixed launcher) it turns just enough.
     */
    public static Launch launch(Vector2d spot, Vector2d target, double currentHeading, RouteTuning t) {
        double aim = FieldPositions.headingTo(spot, target);
        double facing = Math.toRadians(t.launchFacing);
        double arc = Math.toRadians(t.launchArc);
        double turret = wrap(aim - (currentHeading + facing)); // turret angle if the robot doesn't turn
        double heading = currentHeading;
        if (Math.abs(turret) > arc) {
            turret = Math.copySign(arc, turret);
            heading = aim - facing - turret;
        }
        return new Launch(new Pose2d(spot, heading), Math.toDegrees(turret));
    }

    /**
     * Pick up POLLEN at target with the intake pointing in direction intakeFacing (toward the POLLEN).
     * Uses the intake side that needs the least turn from currentHeading, and stops intakeReach short.
     */
    public static Pickup pickup(Vector2d target, double intakeFacing, double currentHeading, RouteTuning t) {
        int best = 0;
        double bestTurn = Double.MAX_VALUE;
        for (int i = 0; i < t.intakeSides.length; i++) {
            double turn = Math.abs(wrap(intakeFacing - Math.toRadians(t.intakeSides[i]) - currentHeading));
            if (turn < bestTurn) {
                bestTurn = turn;
                best = i;
            }
        }
        double heading = intakeFacing - Math.toRadians(t.intakeSides[best]);
        Vector2d spot = target.minus(unit(intakeFacing).times(t.intakeReach));
        return new Pickup(new Pose2d(spot, heading), best);
    }

    public static Vector2d unit(double angle) {
        return new Vector2d(Math.cos(angle), Math.sin(angle));
    }

    /** Angle wrapped to [-pi, pi]. */
    public static double wrap(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }
}
