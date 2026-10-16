package org.firstinspires.ftc.teamcode.field;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

/**
 * BIOBUZZ (2026-27) field positions, in Road Runner coordinates.
 *
 * Frame (matches MeepMeepTesting/field.png): inches, origin at the field center, viewed from
 * the RED alliance station. Red alliance wall at the bottom (y = -72), blue at the top (y = +72),
 * audience on the right (x = +72). Heading 0 faces +x (toward the audience), counterclockwise positive.
 * Note the Competition Manual draws the field from the audience instead (rotated 90 degrees).
 *
 * Red half is y < 0 (tile columns A-C), blue half is y > 0 (D-F). Positions here are for RED;
 * use Alliance.pose()/point() for blue. Measured from field.png and Competition Manual TU04
 * figures: FIELD tolerance is +/- 1 in, so verify against a real field before trusting to the inch.
 */
public final class FieldPositions {
    public static final double TILE = 24;
    public static final double WALL = 72;            // field edge, inside face of perimeter

    // ---- HIVE (center structure). Pivot axis is 43.95 in above the tiles. ----
    public static final double HIVE_PIVOT_HEIGHT = 43.95;
    public static final Vector2d HIVE_CENTER = new Vector2d(0, 0);
    /** Red CELL facing up at the start of the match (audience side). Aim here to LAUNCH. */
    public static final Vector2d CELL_START_UP = new Vector2d(11, -13);
    /** Red CELL facing down at the start; it faces up after each odd number of TIPS. */
    public static final Vector2d CELL_START_DOWN = new Vector2d(-16, -13);

    // ---- AprilTag clusters on the bottom of each CELL (SDK 12: getCurrentGameTagLibrary()) ----
    public static final int[] TAGS_RED_FAR_CELL = {30, 31, 32, 33};
    public static final int[] TAGS_RED_AUDIENCE_CELL = {34, 35, 36, 37};
    public static final int[] TAGS_BLUE_AUDIENCE_CELL = {38, 39, 40, 41};
    public static final int[] TAGS_BLUE_FAR_CELL = {42, 43, 44, 45};

    // ---- FLOWERS (wall-mounted, opening 21.5 in high). Not alliance-owned; absolute positions. ----
    public static final Vector2d FLOWER_RED_WALL = new Vector2d(24, -69.5);
    public static final Vector2d FLOWER_BLUE_WALL = new Vector2d(-24, 69.5);
    public static final Vector2d FLOWER_AUDIENCE_WALL = new Vector2d(69.5, 24);
    public static final Vector2d FLOWER_FAR_WALL = new Vector2d(-69.5, -24);

    // ---- Red zones (mirror for blue) ----
    /** LOADING ZONE: 23 x 11 in against the red wall, x -48..-24, y -72..-61. PARK here. */
    public static final Vector2d LOADING_ZONE = new Vector2d(-36, -66.5);
    /** GARDEN: 2 x 23 in strip on the audience wall, red corner, x 70..72, y -72..-49. */
    public static final Vector2d GARDEN = new Vector2d(71, -60.5);
    /** Where an intake meets the GARDEN POLLEN line. Estimate: 7 in short of the audience wall. */
    public static final Vector2d GARDEN_POLLEN = new Vector2d(65, -62);

    // ---- Red starting spots: on the red half, touching a wall, clear of FLOWERS and LOADING ZONE ----
    // A start is a point on the wall plus the direction into the field; Approach.againstWall() places
    // a robot of any size and heading there.
    /** Red alliance wall, centered. */
    public static final WallSpot START_ALLIANCE_WALL = new WallSpot(new Vector2d(0, -WALL), 90);
    /** Audience wall, red half. */
    public static final WallSpot START_AUDIENCE_WALL = new WallSpot(new Vector2d(WALL, -36), 180);
    /** Audience wall in line with the up CELL. Both CELLS sit on this line. */
    public static final WallSpot START_FACING_UP_CELL = new WallSpot(new Vector2d(WALL, -13), 180);
    /** Far wall, red half, between the LOADING ZONE and the far-wall FLOWER. */
    public static final WallSpot START_FAR_WALL = new WallSpot(new Vector2d(-WALL, -48), 0);

    // ---- Red end-of-AUTO / end-of-match parking ----
    /** Partly inside the LOADING ZONE (PARK scores when the robot is at least partially in it). */
    public static final Pose2d PARK_LOADING_ZONE = new Pose2d(-36, -60, Math.toRadians(90));

    private FieldPositions() {}

    /** A point on the field wall and the direction (degrees) pointing from it into the field. */
    public static final class WallSpot {
        public final Vector2d contact;
        public final double intoField; // radians

        public WallSpot(Vector2d contact, double intoFieldDegrees) {
            this.contact = contact;
            this.intoField = Math.toRadians(intoFieldDegrees);
        }
    }

    /** Heading (radians) that points the robot's front from {@code from} toward {@code target}. */
    public static double headingTo(Vector2d from, Vector2d target) {
        return Math.atan2(target.y - from.y, target.x - from.x);
    }
}
