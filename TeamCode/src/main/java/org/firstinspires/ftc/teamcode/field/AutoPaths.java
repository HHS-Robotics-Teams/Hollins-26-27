package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;

/**
 * AUTO routes shared by the robot and MeepMeep. Each route takes a function that starts a
 * TrajectoryActionBuilder at a pose (MecanumDrive::actionBuilder on the robot, the MeepMeep drive
 * shim on the desktop), the robot's RouteTuning and its Mechanisms, so the same code plans and runs
 * the path on any robot. Routes never hard-code robot headings or sizes: they go through Approach.
 *
 * Write routes in the red frame (FieldPositions) and mirror every pose with the alliance at the end.
 * To add a route: write a method here with the same parameters, then list it in Routes.
 */
public final class AutoPaths {
    public interface BuilderFactory {
        TrajectoryActionBuilder at(Pose2d start);
    }

    private AutoPaths() {}

    /**
     * Start against the audience wall facing the up CELL (3 POLLEN loaded, 4th on the tiles),
     * LAUNCH, collect the GARDEN POLLEN, LAUNCH again, then PARK in the LOADING ZONE.
     * Both CELLS lie on the launch line, so the second volley needs no turn even after a TIP.
     */
    public static Action hiveSide(BuilderFactory drive, Alliance alliance, RouteTuning t, Mechanisms m) {
        Vector2d cell = FieldPositions.CELL_START_UP;
        FieldPositions.WallSpot wall = FieldPositions.START_FACING_UP_CELL;

        Pose2d start = Approach.againstWall(wall.contact, wall.intoField, Approach.aimHeading(wall.contact, cell, t), t);
        // launch from the audience side of the CELL, on the start line
        Approach.Launch launch1 = Approach.launch(
                Approach.launchSpot(cell, 0, t), cell, start.heading.toDouble(), t);
        // GARDEN POLLEN line along the audience wall from the red-wall corner; intake points at the wall
        Approach.Pickup garden = Approach.pickup(
                FieldPositions.GARDEN_POLLEN, 0, launch1.pose.heading.toDouble(), t);
        Approach.Launch launch2 = Approach.launch(
                launch1.pose.position, cell, garden.pose.heading.toDouble(), t);
        // drop below the HIVE frame (about 50 x 39 in at its base) before crossing to PARK
        Vector2d aroundHive = new Vector2d(launch1.pose.position.x, -42);
        Pose2d park = FieldPositions.PARK_LOADING_ZONE;

        Pose2d l1 = alliance.pose(launch1.pose);
        Pose2d g = alliance.pose(garden.pose);
        Pose2d l2 = alliance.pose(launch2.pose);
        Pose2d p = alliance.pose(park);
        return drive.at(alliance.pose(start))
                .strafeToLinearHeading(l1.position, l1.heading)
                .stopAndAdd(m.launch(launch1.aimDegrees))
                .strafeToLinearHeading(g.position, g.heading)
                .stopAndAdd(m.intake(garden.side))
                .strafeToLinearHeading(l2.position, l2.heading)
                .stopAndAdd(m.launch(launch2.aimDegrees))
                .strafeToLinearHeading(alliance.point(aroundHive), p.heading)
                .strafeTo(p.position)
                .build();
    }
}
