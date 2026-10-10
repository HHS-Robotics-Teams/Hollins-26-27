package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;

/**
 * AUTO routes shared by the robot and MeepMeep. Each route takes a function that starts a
 * TrajectoryActionBuilder at a pose (MecanumDrive::actionBuilder on the robot, the MeepMeep drive
 * shim on the desktop) plus the mechanism Actions, so the same code plans and runs the path.
 */
public final class AutoPaths {
    public interface BuilderFactory {
        TrajectoryActionBuilder at(Pose2d start);
    }

    /** Distance from the CELL where the launcher scores. TODO: measure with the real launcher. */
    public static double LAUNCH_DISTANCE = 30;

    private AutoPaths() {}

    /**
     * Start against the audience wall facing the up CELL (3 POLLEN loaded, 4th on the tiles),
     * LAUNCH, collect the GARDEN POLLEN, LAUNCH again, then PARK in the LOADING ZONE.
     * Both CELLS lie on the launch line, so the second volley needs no turn even after a TIP.
     */
    public static Action hiveSide(BuilderFactory drive, Alliance alliance, Action launch, Action intake) {
        Pose2d start = alliance.pose(FieldPositions.START_FACING_UP_CELL);
        Vector2d launchSpot = alliance.point(new Vector2d(
                FieldPositions.CELL_START_UP.x + LAUNCH_DISTANCE, FieldPositions.CELL_START_UP.y));
        // GARDEN POLLEN line along the audience wall from the red-wall corner; front faces the wall
        Pose2d garden = alliance.pose(new Pose2d(56, -62, 0));
        // drop below the HIVE frame (about 50 x 39 in at its base) before crossing to PARK
        Vector2d aroundHive = alliance.point(new Vector2d(FieldPositions.CELL_START_UP.x + LAUNCH_DISTANCE, -42));
        Pose2d park = alliance.pose(FieldPositions.PARK_LOADING_ZONE);

        return drive.at(start)
                .strafeTo(launchSpot)
                .stopAndAdd(launch)
                .strafeToLinearHeading(garden.position, garden.heading)
                .stopAndAdd(intake)
                .strafeToLinearHeading(launchSpot, start.heading)
                .stopAndAdd(launch)
                .strafeToLinearHeading(aroundHive, park.heading)
                .strafeTo(park.position)
                .build();
    }
}
