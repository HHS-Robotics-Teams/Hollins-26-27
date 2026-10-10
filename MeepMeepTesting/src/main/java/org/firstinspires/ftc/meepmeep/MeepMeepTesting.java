package org.firstinspires.ftc.meepmeep;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Plan Road Runner paths on a desktop field before running them on the robot.
 * Field coordinates are inches with (0, 0) at the field center; heading 0 faces +x.
 *
 * To use this season's field, save a top-down field image as MeepMeepTesting/field.png.
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

        RoadRunnerBotEntity theCube = new DefaultBotBuilder(meepMeep)
                .setConstraints(MAX_VEL, MAX_ACCEL, MAX_ANG_VEL, MAX_ANG_ACCEL, TRACK_WIDTH)
                .setDimensions(18, 18)
                .build();

        // Example path: replace with your autonomous routine
        theCube.runAction(theCube.getDrive().actionBuilder(new Pose2d(-36, -60, Math.toRadians(90)))
                .lineToY(-36)
                .strafeTo(new Vector2d(0, -36))
                .turn(Math.toRadians(90))
                .splineTo(new Vector2d(36, 0), Math.toRadians(90))
                .build());

        setBackground(meepMeep);
        meepMeep.setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(theCube)
                .start();
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
