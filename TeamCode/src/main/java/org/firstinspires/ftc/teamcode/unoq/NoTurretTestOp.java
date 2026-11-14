package org.firstinspires.ftc.teamcode.unoq;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.field.Mechanisms;
import org.firstinspires.ftc.teamcode.robots.Robot;
import org.firstinspires.ftc.teamcode.robots.RobotProfiles;
import org.firstinspires.ftc.teamcode.robots.thecube.TheCube;

import java.util.Locale;

/**
 * PRACTICE ONLY (not in meet builds). Vision check on a robot WITHOUT a turret (theCube): the UNO Q
 * and its camera ride on the robot, find the HIVE cluster, and the robot aims by TURNING ITS CHASSIS.
 *
 * The UNO Q sends an ABSOLUTE heading target (robot heading at the frame + bearing to the target;
 * command flag FLAG_HEADING_TARGET), so camera and I2C delay don't matter: the robot turns to that
 * heading with its own IMU / odometry. The UNO Q's page shows the target, bearing and distance.
 *
 *   left stick: drive / strafe        right stick: turn (always wins over auto-aim)
 *   LEFT TRIGGER (hold): let the UNO Q turn the robot to its heading target
 *   left bumper: slow                 RIGHT BUMPER (hold): allow the UNO Q to fire (same rule as UnoTestTeleOp)
 *
 * Auto-aim also needs "Auto-aim" switched on on the UNO Q page; it stops at once when the trigger is
 * released, the turn stick is moved, or the command is older than MAX_COMMAND_AGE_MS.
 * The robot state reports the robot heading as the "turret" angle and the heading target as the
 * turret target, so the UNO Q page shows the aim the same way it will for a turret.
 *
 * Shown on the Driver Station only for theCube ("NoTurretTestOp [cube]").
 */
public class NoTurretTestOp extends LinearOpMode {
    static final int MAX_COMMAND_AGE_MS = 300;
    // Turn-to-heading: power = KP * error (deg), at least MIN_POWER outside TOLERANCE, at most MAX_POWER.
    static double KP_PER_DEG = 0.015;
    static double MIN_POWER = 0.07;
    static double MAX_POWER = 0.35;
    static double TOLERANCE_DEG = 1.0;

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        Robot robot = RobotProfiles.activeOrNull();
        if (robot == null || !robot.tag.equals(TheCube.TAG)) return;
        manager.register(new OpModeMeta.Builder()
                        .setName("NoTurretTestOp [" + robot.tag + "]")
                        .setGroup("UNO Q")
                        .setFlavor(OpModeMeta.Flavor.TELEOP)
                        .build(),
                new NoTurretTestOp());
    }

    @Override
    public void runOpMode() {
        Robot robot = RobotProfiles.forActiveConfig();
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        Mechanisms mechanisms = robot.mechanisms.create(hardwareMap);
        UnoQ unoq = hardwareMap.get(UnoQ.class, "unoq");

        UnoQLink link = new UnoQLink(unoq);
        UnoQRobotState state = link.state;
        state.opMode = UnoQRobotState.MODE_NO_TURRET_TEST;

        telemetry.addData("Robot", robot.tag + " (" + robot.configName + ")");
        telemetry.addLine("Camera on the UNO Q, no turret: hold LEFT TRIGGER to let the UNO Q turn the robot.");
        telemetry.addLine("Right stick always overrides. Hold RIGHT BUMPER to allow firing.");
        telemetry.update();

        link.start(this::isStopRequested);
        waitForStart();

        UnoTestTeleOp.FireGate fire = new UnoTestTeleOp.FireGate();
        Action launch = null;

        while (opModeIsActive()) {
            PoseVelocity2d vel = drive.updatePoseEstimate();
            Pose2d pose = drive.localizer.getPose();
            double headingDeg = Math.toDegrees(pose.heading.toDouble());

            UnoQCommand cmd = link.command();
            boolean fresh = cmd != null && cmd.isFresh(MAX_COMMAND_AGE_MS);
            boolean haveHeading = fresh && cmd.flag(UnoQCommand.FLAG_HEADING_TARGET);
            double targetDeg = haveHeading ? cmd.turretTargetDeg : headingDeg;
            double errorDeg = wrap(targetDeg - headingDeg);

            double scale = gamepad1.left_bumper ? UnoTestTeleOp.SLOW_SCALE : 1.0;
            double stickTurn = UnoTestTeleOp.deadband(-gamepad1.right_stick_x) * scale;
            boolean aimHeld = gamepad1.left_trigger > 0.5;
            boolean autoAim = aimHeld && haveHeading && stickTurn == 0;
            double turn = autoAim ? turnPower(errorDeg) : stickTurn;

            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(UnoTestTeleOp.deadband(-gamepad1.left_stick_y) * scale,
                            UnoTestTeleOp.deadband(-gamepad1.left_stick_x) * scale),
                    turn));

            boolean fireEnabled = gamepad1.right_bumper;
            if (fire.check(cmd, fresh, fireEnabled, launch != null, state)) {
                launch = fire.launch(mechanisms, cmd, 0);   // the chassis is the aim
            }
            if (launch != null && !launch.run(new TelemetryPacket())) launch = null;

            synchronized (state) {
                state.seq = (state.seq + 1) & 0xFFFF;
                state.xIn = pose.position.x;
                state.yIn = pose.position.y;
                state.headingRad = pose.heading.toDouble();
                state.vxInPerS = vel.linearVel.x;
                state.vyInPerS = vel.linearVel.y;
                state.omegaRadPerS = vel.angVel;
                state.flags = UnoQRobotState.TURRET_VALID | (fireEnabled ? UnoQRobotState.FIRE_ENABLED : 0);
                state.turretDeg = headingDeg;          // the chassis is the turret
                state.turretTargetDeg = targetDeg;
            }

            telemetry.addData("Auto-aim (LT)", !aimHeld ? "trigger up"
                    : stickTurn != 0 ? "overridden by right stick"
                    : !haveHeading ? "held, but no fresh heading target (Auto-aim on the UNO Q page?)"
                    : String.format(Locale.US, "TURNING  error %.1f deg  power %.2f", errorDeg, turn));
            telemetry.addData("Heading", String.format(Locale.US, "%.1f deg  target %s", headingDeg,
                    haveHeading ? String.format(Locale.US, "%.1f deg (error %.1f)", targetDeg, errorDeg) : "none"));
            telemetry.addData("UNO Q command", UnoQLink.describe(cmd, MAX_COMMAND_AGE_MS));
            telemetry.addData("Fire (RB)", (fireEnabled ? "HELD" : "up") + "  shots " + state.shots + " / refused "
                    + state.refused + (launch != null ? "  (launching)" : "") + "  " + fire.lastEvent);
            telemetry.addData("Link", link.describe());
            telemetry.update();
        }
    }

    /** Turn power for a heading error in degrees (CCW positive, as RoadRunner). */
    static double turnPower(double errorDeg) {
        if (Math.abs(errorDeg) <= TOLERANCE_DEG) return 0;
        double p = KP_PER_DEG * errorDeg;
        double mag = Math.max(MIN_POWER, Math.min(MAX_POWER, Math.abs(p)));
        return Math.copySign(mag, errorDeg);
    }

    /** Angle in degrees wrapped to (-180, 180]. */
    static double wrap(double deg) {
        double a = deg % 360;
        if (a > 180) a -= 360;
        if (a <= -180) a += 360;
        return a;
    }
}
