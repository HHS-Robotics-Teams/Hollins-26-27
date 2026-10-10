package org.firstinspires.ftc.meepmeep;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import org.firstinspires.ftc.teamcode.field.Alliance;
import org.firstinspires.ftc.teamcode.field.FieldPositions;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Plan Road Runner paths on a desktop field before running them on the robot.
 * Field frame and named positions: TeamCode/.../field/FieldPositions.java (shared with the robot code).
 *
 * Background: MeepMeepTesting/field.png (BIOBUZZ, drawn from the red alliance station).
 */
public class MeepMeepTesting {
    // theCube constraints; keep in sync with TeamCode/.../profiles/TheCube.java
    static final double MAX_VEL = 50;               // maxWheelVel, in/s
    static final double MAX_ACCEL = 50;             // maxProfileAccel, in/s^2
    static final double MAX_ANG_VEL = Math.PI;      // maxAngVel, rad/s
    static final double MAX_ANG_ACCEL = Math.PI;    // maxAngAccel, rad/s^2
    static final double TRACK_WIDTH = 550 * 0.02284; // trackWidthTicks * inPerTick, in

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        setBackground(meepMeep);
        meepMeep.setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(bot(meepMeep, Alliance.RED))
                .addEntity(bot(meepMeep, Alliance.BLUE))
                .start();
    }

    private static RoadRunnerBotEntity bot(MeepMeep meepMeep, Alliance alliance) {
        RoadRunnerBotEntity bot = new DefaultBotBuilder(meepMeep)
                .setConstraints(MAX_VEL, MAX_ACCEL, MAX_ANG_VEL, MAX_ANG_ACCEL, TRACK_WIDTH)
                .setDimensions(18, 18)
                .setColorScheme(alliance == Alliance.RED ? new ColorSchemeRedDark() : new ColorSchemeBlueDark())
                .build();
        bot.runAction(exampleAuto(bot, alliance));
        return bot;
    }

    // Example AUTO, written for red and mirrored for blue: replace with your routine.
    // Drive out, face the up CELL, LAUNCH the 4 pre-loaded POLLEN, then PARK in the LOADING ZONE.
    private static Action exampleAuto(RoadRunnerBotEntity bot, Alliance alliance) {
        Vector2d launchSpot = new Vector2d(0, -40);
        double aim = FieldPositions.headingTo(launchSpot, FieldPositions.CELL_START_UP);
        Pose2d launchPose = alliance.pose(new Pose2d(launchSpot, aim));

        return bot.getDrive().actionBuilder(alliance.pose(FieldPositions.START_ALLIANCE_WALL))
                .strafeToLinearHeading(launchPose.position, launchPose.heading)
                .waitSeconds(2) // LAUNCH
                .strafeToLinearHeading(alliance.pose(FieldPositions.PARK_LOADING_ZONE).position,
                        alliance.pose(FieldPositions.PARK_LOADING_ZONE).heading)
                .build();
    }

    private static void setBackground(MeepMeep meepMeep) {
        File field = new File("field.png");
        if (!field.exists()) field = new File("MeepMeepTesting/field.png");
        if (field.exists()) {
            try {
                meepMeep.setBackground(ImageIO.read(field));
                return;
            } catch (IOException e) {
                System.err.println("Could not read " + field + ": " + e.getMessage());
            }
        }
        meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
    }
}
