package org.firstinspires.ftc.teamcode.unoq;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.field.Mechanisms;
import org.firstinspires.ftc.teamcode.robots.Robot;
import org.firstinspires.ftc.teamcode.robots.RobotProfiles;

import java.util.Locale;

/**
 * PRACTICE ONLY (not in meet builds). Camera on the UNO Q: the UNO Q aims and asks the robot to fire.
 *
 * The robot drives normally (same sticks as "Drive"). A background thread ({@link UnoQLink}) talks
 * to the UNO Q over I2C (protocol 2): it writes the robot's state (pose, velocity, turret, shots) and
 * reads the UNO Q's short command block (turret target, distance, fire requests). The main loop
 * never waits on I2C.
 *
 * Firing: each new fire request from the UNO Q fires ONCE, through this robot's
 * Mechanisms.launch(turret target, distance) (the robot picks the launcher speed for the distance
 * from its own table), and only while the driver HOLDS RIGHT BUMPER (fire enable).
 * A request that arrives while the bumper is up is counted as refused and dropped: it never fires
 * later. Commands older than MAX_COMMAND_AGE_MS are ignored.
 *
 *   left stick: drive / strafe   right stick: turn   left bumper: slow   RIGHT BUMPER: hold to allow firing
 *
 * Config: I2C device "UNO Q Vision" named "unoq" (see UnoQ).
 */
@TeleOp(name = "UnoTestTeleOp", group = "UNO Q")
public class UnoTestTeleOp extends LinearOpMode {
    static final int MAX_COMMAND_AGE_MS = 300;
    static final double DEADBAND = 0.05, SLOW_SCALE = 0.35;

    @Override
    public void runOpMode() {
        Robot robot = RobotProfiles.forActiveConfig();
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        Mechanisms mechanisms = robot.mechanisms.create(hardwareMap);
        UnoQ unoq = hardwareMap.get(UnoQ.class, "unoq");
        unoq.setFastBus(true);   // no faster on REV hubs (see M6), but harmless

        UnoQLink link = new UnoQLink(unoq);
        UnoQRobotState state = link.state;
        state.opMode = UnoQRobotState.MODE_UNO_TEST_TELEOP;

        telemetry.addData("Robot", robot.tag + " (" + robot.configName + ")");
        telemetry.addData("Mechanisms", mechanisms.describe());
        telemetry.addLine("Camera on the UNO Q. Hold RIGHT BUMPER to allow the UNO Q to fire.");
        telemetry.update();

        link.start(this::isStopRequested);
        waitForStart();

        FireGate fire = new FireGate();
        Action launch = null;

        while (opModeIsActive()) {
            double scale = gamepad1.left_bumper ? SLOW_SCALE : 1.0;
            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(deadband(-gamepad1.left_stick_y) * scale, deadband(-gamepad1.left_stick_x) * scale),
                    deadband(-gamepad1.right_stick_x) * scale));
            PoseVelocity2d vel = drive.updatePoseEstimate();
            Pose2d pose = drive.localizer.getPose();
            boolean fireEnabled = gamepad1.right_bumper;

            UnoQCommand cmd = link.command();
            boolean fresh = cmd != null && cmd.isFresh(MAX_COMMAND_AGE_MS);
            double turretTarget = fresh && cmd.flag(UnoQCommand.FLAG_TURRET_VALID) ? cmd.turretTargetDeg : 0;

            if (fire.check(cmd, fresh, fireEnabled, launch != null, state)) {
                launch = fire.launch(mechanisms, cmd, turretTarget);
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
                state.flags = fireEnabled ? UnoQRobotState.FIRE_ENABLED : 0;   // no turret/launcher sensors yet
                state.turretTargetDeg = turretTarget;
            }

            telemetry.addData("Fire enable (RB)", fireEnabled ? "HELD" : "up");
            telemetry.addData("UNO Q command", UnoQLink.describe(cmd, MAX_COMMAND_AGE_MS));
            telemetry.addData("Shots / refused", state.shots + " / " + state.refused + (launch != null ? "  (launching)" : ""));
            telemetry.addData("Last", fire.lastEvent);
            telemetry.addData("Link", link.describe());
            telemetry.addData("Pose", String.format(Locale.US, "x %.1f  y %.1f in  %.1f deg",
                    pose.position.x, pose.position.y, Math.toDegrees(pose.heading.toDouble())));
            telemetry.update();
        }
    }

    static double deadband(double v) {
        return Math.abs(v) < DEADBAND ? 0 : v;
    }

    /**
     * The fire-once rule shared by the practice op modes: each NEW fire_seq from a fresh command fires
     * once, only while fire enable is held and no launch is running; otherwise it is counted as
     * refused and dropped. Requests already pending when the op mode starts are ignored.
     */
    static final class FireGate {
        private Integer lastFireSeq = null;
        String lastEvent = "";

        /** True when this command is a new fire request that should fire now (counts are updated). */
        boolean check(UnoQCommand cmd, boolean fresh, boolean enabled, boolean launching, UnoQRobotState state) {
            if (!fresh) return false;
            if (lastFireSeq == null) {
                lastFireSeq = cmd.fireSeq;
                return false;
            }
            if (cmd.fireSeq == lastFireSeq) return false;
            lastFireSeq = cmd.fireSeq;
            if (!enabled) {
                synchronized (state) { state.refused++; }
                lastEvent = "fire " + cmd.fireSeq + " REFUSED (hold right bumper)";
                return false;
            }
            if (launching) {
                synchronized (state) { state.refused++; }
                lastEvent = "fire " + cmd.fireSeq + " refused: still launching";
                return false;
            }
            synchronized (state) { state.shots++; state.ackFireSeq = cmd.fireSeq; }
            return true;
        }

        /** Start the launch for an accepted request, with the distance when the UNO Q sent one. */
        Action launch(Mechanisms mechanisms, UnoQCommand cmd, double aimDeg) {
            boolean haveDistance = cmd.flag(UnoQCommand.FLAG_DISTANCE_VALID);
            lastEvent = String.format(Locale.US, "fire %d at %.1f deg, %s", cmd.fireSeq, aimDeg,
                    haveDistance ? String.format(Locale.US, "%.1f in", cmd.distanceIn) : "no distance");
            return haveDistance ? mechanisms.launch(aimDeg, cmd.distanceIn) : mechanisms.launch(aimDeg);
        }
    }
}
