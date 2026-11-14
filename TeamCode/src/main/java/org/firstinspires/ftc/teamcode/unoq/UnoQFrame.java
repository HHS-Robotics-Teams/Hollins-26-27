package org.firstinspires.ftc.teamcode.unoq;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/** One decoded read of the UNO Q out block (layout in {@link UnoQ}). */
public class UnoQFrame {
    // status bits
    public static final int STATUS_CAMERA_OK = 0x01;
    public static final int STATUS_LINUX_OK = 0x02;
    public static final int STATUS_STALE = 0x04;
    public static final int STATUS_ROBOT_INPUT_FRESH = 0x08;
    public static final int STATUS_TEST_PATTERN = 0x80;   // M6 bench test: values are a fixed pattern

    public static class Tag {
        public int id, flags;
        public double xIn, yIn, zIn, bearingDeg, distStdIn;

        public double distanceIn() {
            return Math.sqrt(xIn * xIn + yIn * yIn + zIn * zIn);
        }
    }

    public static class Ball {
        public double distanceIn, bearingDeg;
        public int color, confidence;
    }

    public final byte[] raw;
    public final double readMs;
    public int bytesRead;        // how many bytes the I2C read returned (may be less than the block)

    public int version, status, frame, ageMs, robotSeqEcho, tagCount, ballCount;
    public int checksum, computedChecksum;
    public final List<Tag> tags = new ArrayList<>();
    public final List<Ball> balls = new ArrayList<>();

    // extra block (only after UnoQ.readExtra)
    public boolean hasExtra;
    public double fieldXIn, fieldYIn, fieldHeadingRad;
    public int fieldAgeMs, fieldQuality;
    public int targetSeq, targetKind, targetValidMs, targetConfidence;
    public double targetXIn, targetYIn, targetHeadingRad;

    private UnoQFrame(byte[] raw, double readMs) {
        this.raw = raw;
        this.readMs = readMs;
        this.bytesRead = raw.length;
    }

    /** Checksum matches and the version is ours: the bytes are trustworthy. */
    public boolean isValid() {
        return bytesRead == UnoQ.MAIN_LENGTH && version == UnoQ.PROTOCOL_VERSION && checksum == computedChecksum;
    }

    /** All 0x00 or all 0xFF: nothing answered (wiring, address, or the sketch isn't running). */
    public boolean looksDisconnected() {
        boolean allZero = true, allFF = true;
        for (byte b : raw) {
            allZero &= b == 0;
            allFF &= b == (byte) 0xFF;
        }
        return raw.length == 0 || allZero || allFF;
    }

    public boolean status(int bit) {
        return (status & bit) != 0;
    }

    static UnoQFrame parse(byte[] raw, double readMs) {
        UnoQFrame f = new UnoQFrame(raw, readMs);
        if (raw.length < UnoQ.MAIN_LENGTH) return f;
        ByteBuffer b = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        f.version = b.get(0x00) & 0xFF;
        f.status = b.get(0x01) & 0xFF;
        f.frame = b.getShort(0x02) & 0xFFFF;
        f.ageMs = b.getShort(0x04) & 0xFFFF;
        f.robotSeqEcho = b.getShort(0x06) & 0xFFFF;
        f.tagCount = Math.min(b.get(0x08) & 0xFF, UnoQ.MAX_TAGS);
        f.ballCount = Math.min(b.get(0x09) & 0xFF, UnoQ.MAX_BALLS);
        f.checksum = b.getShort(0x0A) & 0xFFFF;
        int sum = 0;
        for (int i = 0x0C; i < UnoQ.MAIN_LENGTH; i++) sum += raw[i] & 0xFF;
        f.computedChecksum = sum & 0xFFFF;

        for (int i = 0; i < f.tagCount; i++) {
            int o = 0x0C + 12 * i;
            Tag t = new Tag();
            t.id = b.get(o) & 0xFF;
            t.flags = b.get(o + 1) & 0xFF;
            t.xIn = b.getShort(o + 2) / 25.4;
            t.yIn = b.getShort(o + 4) / 25.4;
            t.zIn = b.getShort(o + 6) / 25.4;
            t.bearingDeg = b.getShort(o + 8) / 100.0;
            t.distStdIn = (b.getShort(o + 10) & 0xFFFF) / 25.4;
            f.tags.add(t);
        }
        for (int i = 0; i < f.ballCount; i++) {
            int o = 0x3C + 8 * i;
            Ball ball = new Ball();
            ball.distanceIn = (b.getShort(o) & 0xFFFF) / 25.4;
            ball.bearingDeg = b.getShort(o + 2) / 100.0;
            ball.color = b.get(o + 4) & 0xFF;
            ball.confidence = b.get(o + 5) & 0xFF;
            f.balls.add(ball);
        }
        return f;
    }

    void parseExtra(byte[] raw) {
        if (raw.length < UnoQ.EXTRA_LENGTH) return;
        ByteBuffer b = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        fieldXIn = b.getShort(0) / 100.0;
        fieldYIn = b.getShort(2) / 100.0;
        fieldHeadingRad = b.getShort(4) / 1000.0;
        fieldAgeMs = b.getShort(6) & 0xFFFF;
        fieldQuality = b.get(8) & 0xFF;
        targetSeq = b.get(12) & 0xFF;
        targetKind = b.get(13) & 0xFF;
        targetXIn = b.getShort(14) / 100.0;
        targetYIn = b.getShort(16) / 100.0;
        targetHeadingRad = b.getShort(18) / 1000.0;
        targetValidMs = b.getShort(20) & 0xFFFF;
        targetConfidence = b.get(22) & 0xFF;
        hasExtra = true;
    }
}
