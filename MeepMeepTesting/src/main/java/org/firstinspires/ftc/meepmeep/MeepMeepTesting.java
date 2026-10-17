package org.firstinspires.ftc.meepmeep;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SleepAction;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import org.firstinspires.ftc.teamcode.field.Alliance;
import org.firstinspires.ftc.teamcode.field.Mechanisms;
import org.firstinspires.ftc.teamcode.field.RouteTuning;
import org.firstinspires.ftc.teamcode.field.Routes;
import org.firstinspires.ftc.teamcode.robots.nerdvana.NerdRoute;
import org.firstinspires.ftc.teamcode.robots.thecube.CubeRoute;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Plan Road Runner paths on a desktop field before running them on the robot.
 * Routes: TeamCode/.../field/Routes.java (the same code the robot runs).
 * Robots: TeamCode/.../robots/<robot>/<Tag>Route.java (size, intake sides, launcher).
 *
 * Background: MeepMeepTesting/field.png (BIOBUZZ, drawn from the red alliance station).
 */
public class MeepMeepTesting {
    // ---- what to simulate ----
    static final Routes ROUTE = Routes.HIVE_SIDE;
    static final RouteTuning ROBOT = NerdRoute.TUNING; // or CubeRoute.TUNING

    // drive constraints (theCube); keep in sync with TeamCode/.../robots/thecube/TheCube.java
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
                .setDimensions(ROBOT.width, ROBOT.length)
                .setColorScheme(alliance == Alliance.RED ? new ColorSchemeRedDark() : new ColorSchemeBlueDark())
                .build();
        bot.runAction(ROUTE.route.build(bot.getDrive()::actionBuilder, alliance, ROBOT, new SimMechanisms(ROBOT)));
        return bot;
    }

    /** Mechanism actions as pauses of the robot's estimated durations. */
    private static final class SimMechanisms implements Mechanisms {
        private final RouteTuning tuning;

        SimMechanisms(RouteTuning tuning) {
            this.tuning = tuning;
        }

        @Override
        public Action launch(double aimDegrees) {
            return new SleepAction(tuning.simLaunchSeconds);
        }

        @Override
        public Action intake(int side) {
            return new SleepAction(tuning.simIntakeSeconds);
        }

        @Override
        public String describe() {
            return "SimMechanisms";
        }
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
