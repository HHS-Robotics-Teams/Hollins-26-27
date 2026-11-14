package org.firstinspires.ftc.teamcode.unoq;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

/**
 * M6 bench test for the UNO Q I2C link (no camera data needed).
 *
 * The UNO Q sketch in test-pattern mode (status bit 0x80) serves fixed values plus a frame counter that
 * advances at ~60 Hz, and echoes the robot_seq this op mode writes. Telemetry shows read rate and time,
 * checksum / version / no-answer errors, whether the frame counter advances, the robot_seq round trip,
 * and the decoded block.
 *
 * Wiring: hub I2C port pin 1 GND, 3 SDA, 4 SCL to the UNO Q (pin 2, 3.3 V, NOT connected; the UNO Q has
 * its own 5 V supply). Config: I2C device "UNO Q Vision" named "unoq" on that port's bus.
 *
 * Buttons: A = 100 / 400 kHz bus, X = also read the extra block (field pose + target), B = reset stats.
 * Pass for M6: 5 minutes with 0 errors, frame counter advancing, robot_seq lag <= 2.
 */
@TeleOp(name = "UNO Q I2C test", group = "Testing")
public class UnoQI2cTest extends LinearOpMode {
    // Test pattern the sketch serves (keep in step with the sketch).
    static final int[] PATTERN_TAG0 = {23, 1, 100, -50, 1500, 381, 2};   // id, flags, x, y, z mm, bearing cdeg, std mm

    @Override
    public void runOpMode() {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);
        UnoQ unoq = hardwareMap.get(UnoQ.class, "unoq");

        boolean fast = false, extra = false;
        boolean lastA = false, lastX = false, lastB = false;
        unoq.setFastBus(fast);

        Stats s = new Stats();
        int robotSeq = 0;

        telemetry.addLine("UNO Q I2C test ready. A: 100/400 kHz  X: extra block  B: reset stats");
        telemetry.update();
        waitForStart();
        s.reset();

        while (opModeIsActive()) {
            if (gamepad1.a && !lastA) { fast = !fast; unoq.setFastBus(fast); s.reset(); }
            if (gamepad1.x && !lastX) { extra = !extra; s.reset(); }
            if (gamepad1.b && !lastB) s.reset();
            lastA = gamepad1.a; lastX = gamepad1.x; lastB = gamepad1.b;

            // write: sequence number + a fake pose that changes slowly, so the UNO Q page shows it moving
            robotSeq = (robotSeq + 1) & 0xFFFF;
            double t = s.elapsedS();
            long w0 = System.nanoTime();
            try {
                unoq.writeRobotState(robotSeq, 10 * Math.sin(t), -20, Math.toRadians(45), 0, 0, 0, 0, 0);
            } catch (RuntimeException e) {
                s.exceptions++;
            }
            s.writeMs.add((System.nanoTime() - w0) / 1e6);

            // read
            UnoQFrame f;
            try {
                f = unoq.read();
                if (extra) unoq.readExtra(f);
            } catch (RuntimeException e) {
                s.exceptions++;
                s.lastError = e.getClass().getSimpleName() + ": " + e.getMessage();
                continue;
            }
            s.add(f, robotSeq);
            show(f, s, fast, extra, robotSeq);
        }
    }

    private void show(UnoQFrame f, Stats s, boolean fast, boolean extra, int robotSeq) {
        telemetry.addData("bus", "%s kHz%s   (A toggles, X extra, B reset)", fast ? "400" : "100", extra ? " +extra" : "");
        telemetry.addData("reads", "%d in %.1f s = %.1f /s", s.reads, s.elapsedS(), s.reads / Math.max(s.elapsedS(), 1e-3));
        telemetry.addData("read ms", "avg %.2f  max %.2f   write ms avg %.2f", s.readMs.avg(), s.readMs.max, s.writeMs.avg());
        telemetry.addData("ERRORS", "checksum %d  version %d  no-answer %d  exceptions %d",
                s.badChecksum, s.badVersion, s.disconnected, s.exceptions);
        if (s.lastError != null) telemetry.addData("last error", s.lastError);
        telemetry.addData("frame", "%d  (+%.1f/s, repeats %d)", f.frame, s.frameRate(), s.sameFrame);
        telemetry.addData("robot_seq", "sent %d  echo %d  lag %d (max %d)", robotSeq, f.robotSeqEcho,
                (robotSeq - f.robotSeqEcho) & 0xFFFF, s.maxLag);
        telemetry.addData("header", "v%d  status 0x%02X%s  age %d ms  tags %d  balls %d",
                f.version, f.status, f.status(UnoQFrame.STATUS_TEST_PATTERN) ? " (TEST PATTERN)" : "",
                f.ageMs, f.tagCount, f.ballCount);
        telemetry.addData("pattern", s.patternOk(f) ? "tag0 matches the test pattern" : "tag0 does NOT match the test pattern");
        for (UnoQFrame.Tag t : f.tags) {
            telemetry.addLine(String.format(Locale.US, "  tag %3d  %6.1f in  %+6.1f deg  (x %.1f y %.1f z %.1f)  f%d",
                    t.id, t.distanceIn(), t.bearingDeg, t.xIn, t.yIn, t.zIn, t.flags));
        }
        for (UnoQFrame.Ball b : f.balls) {
            telemetry.addLine(String.format(Locale.US, "  ball %6.1f in  %+6.1f deg  color %d  conf %d",
                    b.distanceIn, b.bearingDeg, b.color, b.confidence));
        }
        if (f.hasExtra) {
            telemetry.addData("field", "%.2f, %.2f in  %.3f rad  age %d  q %d", f.fieldXIn, f.fieldYIn,
                    f.fieldHeadingRad, f.fieldAgeMs, f.fieldQuality);
            telemetry.addData("target", "seq %d kind %d  %.2f, %.2f in  %.3f rad  valid %d ms",
                    f.targetSeq, f.targetKind, f.targetXIn, f.targetYIn, f.targetHeadingRad, f.targetValidMs);
        }
        telemetry.update();
    }

    /** Running numbers for the test. */
    static class Stats {
        long t0 = System.nanoTime();
        int reads, badChecksum, badVersion, disconnected, exceptions, sameFrame, maxLag;
        int firstFrame = -1, lastFrame = -1, framesAdvanced;
        String lastError;
        Avg readMs = new Avg(), writeMs = new Avg();

        void reset() {
            t0 = System.nanoTime();
            reads = badChecksum = badVersion = disconnected = exceptions = sameFrame = maxLag = 0;
            firstFrame = lastFrame = -1;
            framesAdvanced = 0;
            lastError = null;
            readMs = new Avg();
            writeMs = new Avg();
        }

        double elapsedS() {
            return (System.nanoTime() - t0) / 1e9;
        }

        void add(UnoQFrame f, int robotSeq) {
            reads++;
            readMs.add(f.readMs);
            if (f.looksDisconnected()) { disconnected++; return; }
            if (f.version != UnoQ.PROTOCOL_VERSION) badVersion++;
            else if (f.checksum != f.computedChecksum) badChecksum++;
            if (lastFrame >= 0) {
                int d = (f.frame - lastFrame) & 0xFFFF;
                if (d == 0) sameFrame++;
                framesAdvanced += d;
            }
            lastFrame = f.frame;
            if (reads > 5) maxLag = Math.max(maxLag, (robotSeq - f.robotSeqEcho) & 0xFFFF);
        }

        double frameRate() {
            return framesAdvanced / Math.max(elapsedS(), 1e-3);
        }

        boolean patternOk(UnoQFrame f) {
            if (f.tags.isEmpty()) return false;
            UnoQFrame.Tag t = f.tags.get(0);
            int[] p = PATTERN_TAG0;
            return t.id == p[0] && t.flags == p[1]
                    && Math.round(t.xIn * 25.4) == p[2] && Math.round(t.yIn * 25.4) == p[3]
                    && Math.round(t.zIn * 25.4) == p[4] && Math.round(t.bearingDeg * 100) == p[5];
        }
    }

    static class Avg {
        double sum, max;
        int n;

        void add(double v) {
            sum += v;
            n++;
            max = Math.max(max, v);
        }

        double avg() {
            return n == 0 ? 0 : sum / n;
        }
    }
}
