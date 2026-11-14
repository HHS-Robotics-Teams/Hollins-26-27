package org.firstinspires.ftc.teamcode.unoq;

import java.util.Locale;
import java.util.function.BooleanSupplier;

/**
 * Background I2C link to the UNO Q for the practice op modes (protocol 2): about 50 times a second
 * it writes the robot state and reads the command block. The op mode's loop never waits on I2C:
 * it updates {@link #state} (inside {@code synchronized (link.state)}) and reads {@link #command()}.
 */
public class UnoQLink {
    public static final long PERIOD_MS = 20;   // ~50 Hz: one 26-byte write + one 10-byte read

    public final UnoQRobotState state = new UnoQRobotState();

    private final UnoQ unoq;
    private volatile UnoQCommand command;
    private volatile String error = "";
    private volatile double hz, writeMs, readMs;
    private volatile int errors;

    public UnoQLink(UnoQ unoq) {
        this.unoq = unoq;
    }

    /** Start the background thread; it stops when {@code stop} returns true (e.g. isStopRequested). */
    public void start(BooleanSupplier stop) {
        Thread t = new Thread(() -> loop(stop), "UnoQLink");
        t.setDaemon(true);
        t.start();
    }

    /** Latest command read from the UNO Q, or null before the first read. */
    public UnoQCommand command() {
        return command;
    }

    /** One telemetry line: rate, write / read time, errors. */
    public String describe() {
        return String.format(Locale.US, "%.0f Hz  write %.1f ms  read %.1f ms  errors %d %s",
                hz, writeMs, readMs, errors, error);
    }

    /** One telemetry line for a command (or "none yet"). */
    public static String describe(UnoQCommand cmd, int maxAgeMs) {
        if (cmd == null) return "none yet";
        String target = cmd.flag(UnoQCommand.FLAG_HEADING_TARGET) ? String.format(Locale.US, "heading %.1f deg", cmd.turretTargetDeg)
                : cmd.flag(UnoQCommand.FLAG_TURRET_VALID) ? String.format(Locale.US, "turret %.1f deg", cmd.turretTargetDeg)
                : "no target";
        return String.format(Locale.US, "%s  age %d ms  %s  dist %s  fire_seq %d  camera %s",
                cmd.isFresh(maxAgeMs) ? "fresh" : (cmd.isValid() ? "STALE" : "INVALID"), cmd.ageMs, target,
                cmd.flag(UnoQCommand.FLAG_DISTANCE_VALID) ? String.format(Locale.US, "%.1f in", cmd.distanceIn) : "none",
                cmd.fireSeq, cmd.flag(UnoQCommand.FLAG_CAMERA_ON_UNOQ) ? "on UNO Q" : "not on UNO Q");
    }

    private void loop(BooleanSupplier stop) {
        long windowStart = System.nanoTime();
        int count = 0;
        while (!stop.getAsBoolean()) {
            long t0 = System.nanoTime();
            try {
                UnoQRobotState snap;
                synchronized (state) { snap = state.copy(); }
                unoq.writeRobotState(snap);
                long t1 = System.nanoTime();
                UnoQCommand cmd = unoq.readCommand();
                writeMs = (t1 - t0) / 1e6;
                readMs = cmd.readMs;
                command = cmd;
                error = "";
            } catch (RuntimeException e) {
                errors++;
                error = e.getClass().getSimpleName();
            }
            count++;
            long now = System.nanoTime();
            if (now - windowStart >= 1_000_000_000L) {
                hz = count * 1e9 / (now - windowStart);
                count = 0;
                windowStart = now;
            }
            long sleepMs = PERIOD_MS - (System.nanoTime() - t0) / 1_000_000;
            if (sleepMs > 0) {
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
