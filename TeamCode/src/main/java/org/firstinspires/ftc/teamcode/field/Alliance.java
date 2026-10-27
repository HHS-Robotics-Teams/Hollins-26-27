package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

/**
 * The BIOBUZZ field is rotationally symmetric: everything on the blue side is the red side
 * rotated 180 degrees about the field center. FieldPositions are written for red; use
 * {@code alliance.pose(...)} / {@code alliance.point(...)} to get the same spot for either alliance.
 */
public enum Alliance {
    RED,
    BLUE;

    public Pose2d pose(Pose2d red) {
        if (this == RED) return red;
        return new Pose2d(-red.position.x, -red.position.y, red.heading.toDouble() + Math.PI);
    }

    public Vector2d point(Vector2d red) {
        if (this == RED) return red;
        return new Vector2d(-red.x, -red.y);
    }

    /** AprilTag IDs on this alliance's CELL that starts facing up (points away from its FLOWER). */
    public int[] startUpCellTags() {
        return this == RED ? FieldPositions.TAGS_RED_AUDIENCE_CELL : FieldPositions.TAGS_BLUE_FAR_CELL;
    }

    /** AprilTag IDs on this alliance's CELL that starts facing down. */
    public int[] startDownCellTags() {
        return this == RED ? FieldPositions.TAGS_RED_FAR_CELL : FieldPositions.TAGS_BLUE_AUDIENCE_CELL;
    }
}
