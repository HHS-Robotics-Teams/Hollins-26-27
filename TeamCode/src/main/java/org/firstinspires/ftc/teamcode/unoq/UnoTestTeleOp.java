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
 * The robot drives normally (same sticks as "Drive"). A background thread talks to the UNO Q over
 * I2C (protocol 2): it writes the robot's state (pose, velocity, turret, shots) and reads the UNO Q's
 * short command block (turret target + fire requests). The main loop never waits on I2C.
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
    static final long LINK_PERIOD_MS = 20;          // ~50 Hz: one 26-byte write + one 10-byte read
    static final double DEADBAND = 0.05, SLOW_SCALE = 0.35;

    private final UnoQRobotState state = new UnoQRobotState();
    private volatile UnoQCommand lastCommand;
    private volatile String linkError = "";
    private volatile double linkHz, writeMs, readMs;
    private volatile int linkErrors;

    @Override
    public void runOpMode() {
        Robot robot = RobotProfiles.forActiveConfig();
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        Mechanisms mechanisms = robot.mechanisms.create(hardwareMap);
        UnoQ unoq = hardwareMap.get(UnoQ.class, "unoq");
        unoq.setFastBus(true);   // no faster on REV hubs (see M6), but harmless

        state.opMode = UnoQRobotState.MODE_UNO_TEST_TELEOP;

        telemetry.addData("Robot", robot.tag + " (" + robot.configName + ")");
        telemetry.addData("Mechanisms", mechanisms.describe());
        telemetry.addLine("Camera on the UNO Q. Hold RIGHT BUMPER to allow the UNO Q to fire.");
        telemetry.update();

        Thread link = new Thread(() -> linkLoop(unoq), "UnoQLink");
        link.setDaemon(true);
        link.start();

        waitForStart();

        Integer lastFireSeq = null;   // fire requests already pending at start are not fired
        Action launch = null;
        String lastEvent = "";

        while (opModeIsActive()) {
            double scale = gamepad1.left_bumper ? SLOW_SCALE : 1.0;
            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(deadband(-gamepad1.left_stick_y) * scale, deadband(-gamepad1.left_stick_x) * scale),
                    deadband(-gamepad1.right_stick_x) * scale));
            PoseVelocity2d vel = drive.updatePoseEstimate();
            Pose2d pose = drive.localizer.getPose();
            boolean fireEnabled = gamepad1.right_bumper;

            UnoQCommand cmd = lastCommand;
            boolean fresh = cmd != null && cmd.isFresh(MAX_COMMAND_AGE_MS);
            double turretTarget = fresh && cmd.flag(UnoQCommand.FLAG_TURRET_VALID) ? cmd.turretTargetDeg : 0;
            boolean haveDistance = fresh && cmd.flag(UnoQCommand.FLAG_DISTANCE_VALID);

            if (fresh) {
                if (lastFireSeq == null) {
                    lastFireSeq = cmd.fireSeq;
                } else if (cmd.fireSeq != lastFireSeq) {
                    lastFireSeq = cmd.fireSeq;
                    if (!fireEnabled) {
                        synchronized (state) { state.refused++; }
                        lastEvent = "fire " + cmd.fireSeq + " REFUSED (hold right bumper)";
                    } else if (launch != null) {
                        synchronized (state) { state.refused++; }
                        lastEvent = "fire " + cmd.fireSeq + " refused: still launching";
                    } else {
                        launch = haveDistance ? mechanisms.launch(turretTarget, cmd.distanceIn)
                                              : mechanisms.launch(turretTarget);
                        synchronized (state) { state.shots++; state.ackFireSeq = cmd.fireSeq; }
                        lastEvent = String.format(Locale.US, "fire %d at %.1f deg, %s", cmd.fireSeq, turretTarget,
                                haveDistance ? String.format(Locale.US, "%.1f in", cmd.distanceIn) : "no distance");
                    }
                }
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
            telemetry.addData("UNO Q command", cmd == null ? "none yet"
                    : String.format(Locale.US, "%s  age %d ms  turret %.1f deg  dist %s  fire_seq %d  camera %s",
                    fresh ? "fresh" : (cmd.isValid() ? "STALE" : "INVALID"), cmd.ageMs, cmd.turretTargetDeg,
                    cmd.flag(UnoQCommand.FLAG_DISTANCE_VALID) ? String.format(Locale.US, "%.1f in", cmd.distanceIn) : "none",
                    cmd.fireSeq, cmd.flag(UnoQCommand.FLAG_CAMERA_ON_UNOQ) ? "on UNO Q" : "not on UNO Q"));
            telemetry.addData("Shots / refused", state.shots + " / " + state.refused + (launch != null ? "  (launching)" : ""));
            telemetry.addData("Last", lastEvent);
            telemetry.addData("Link", String.format(Locale.US, "%.0f Hz  write %.1f ms  read %.1f ms  errors %d %s",
                    linkHz, writeMs, readMs, linkErrors, linkError));
            telemetry.addData("Pose", String.format(Locale.US, "x %.1f  y %.1f in  %.1f deg",
                    pose.position.x, pose.position.y, Math.toDegrees(pose.heading.toDouble())));
            telemetry.update();
        }
    }

    /** Background I2C: write the state, read the command, ~50 Hz. Never blocks the drive loop. */
    private void linkLoop(UnoQ unoq) {
        long windowStart = System.nanoTime();
        int count = 0;
        while (!isStopRequested()) {
            long t0 = System.nanoTime();
            try {
                UnoQRobotState snap;
                synchronized (state) { snap = state.copy(); }
                unoq.writeRobotState(snap);
                long t1 = System.nanoTime();
                UnoQCommand cmd = unoq.readCommand();
                writeMs = (t1 - t0) / 1e6;
                readMs = cmd.readMs;
                lastCommand = cmd;
                linkError = "";
            } catch (RuntimeException e) {
                linkErrors++;
                linkError = e.getClass().getSimpleName();
            }
            count++;
            long now = System.nanoTime();
            if (now - windowStart >= 1_000_000_000L) {
                linkHz = count * 1e9 / (now - windowStart);
                count = 0;
                windowStart = now;
            }
            long sleepMs = LINK_PERIOD_MS - (System.nanoTime() - t0) / 1_000_000;
            if (sleepMs > 0) sleep(sleepMs);
        }
    }

    private static double deadband(double v) {
        return Math.abs(v) < DEADBAND ? 0 : v;
    }
}
