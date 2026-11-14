package org.firstinspires.ftc.teamcode.unoq;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Robot -> UNO Q state block (protocol 2, practice only), written at register 0x80, 26 bytes,
 * little-endian. The first 16 bytes are the protocol 1 block unchanged:
 *
 *   0  robot_seq u16 | 2 x_cin i16 | 4 y_cin i16 | 6 heading_mrad i16
 *   8  vx_cin_s i16  | 10 vy_cin_s i16 | 12 omega_mrad_s i16        (robot-frame velocity)
 *   14 mode u8: bits 0-3 op mode (MODE_*), CAMERA_ON_ROBOT, FIRE_ENABLED, TURRET_VALID, LAUNCHER_VALID
 *   15 ack_fire_seq u8 (last fire_seq the robot fired on)
 *   16 turret_cdeg i16 (measured) | 18 turret_target_cdeg i16 (commanded) | 20 launcher_rpm i16
 *   22 shots u8 (fired in this op mode) | 23 refused u8 (fire requests ignored: enable not held)
 *   24 launcher_cmd_rpm i16 (the speed the robot chose, e.g. from its distance table)
 *
 * RoadRunner field frame: inches and radians. Keep in step with the UNO Q sketch.
 */
public class UnoQRobotState {
    public static final int LENGTH = 26;

    // mode, bits 0-3: which op mode is writing
    public static final int MODE_NONE = 0;
    public static final int MODE_UNO_TEST_TELEOP = 1;   // camera on the UNO Q; it sends turret + fire
    public static final int MODE_BOT_TEST_FIRE = 2;     // camera on the robot; robot computes and fires
    public static final int MODE_NO_TURRET_TEST = 3;    // camera on the UNO Q; robot turns its chassis to aim
    // mode, flag bits
    public static final int CAMERA_ON_ROBOT = 0x10;
    public static final int FIRE_ENABLED = 0x20;        // driver is holding the fire-enable button
    public static final int TURRET_VALID = 0x40;        // turret_cdeg is a real measurement
    public static final int LAUNCHER_VALID = 0x80;      // launcher_rpm is a real measurement

    public int seq;
    public double xIn, yIn, headingRad, vxInPerS, vyInPerS, omegaRadPerS;
    public int opMode, flags, ackFireSeq;
    public double turretDeg, turretTargetDeg, launcherRpm, launcherCommandRpm;
    public int shots, refused;

    public byte[] toBytes() {
        ByteBuffer b = ByteBuffer.allocate(LENGTH).order(ByteOrder.LITTLE_ENDIAN);
        b.putShort((short) seq);
        b.putShort(clamp16(xIn * 100));
        b.putShort(clamp16(yIn * 100));
        b.putShort(clamp16(headingRad * 1000));
        b.putShort(clamp16(vxInPerS * 100));
        b.putShort(clamp16(vyInPerS * 100));
        b.putShort(clamp16(omegaRadPerS * 1000));
        b.put((byte) ((opMode & 0x0F) | (flags & 0xF0)));
        b.put((byte) ackFireSeq);
        b.putShort(clamp16(turretDeg * 100));
        b.putShort(clamp16(turretTargetDeg * 100));
        b.putShort(clamp16(launcherRpm));
        b.put((byte) Math.min(shots, 255));
        b.put((byte) Math.min(refused, 255));
        b.putShort(clamp16(launcherCommandRpm));
        return b.array();
    }

    /** A copy, so the link thread can send one consistent snapshot while the op mode keeps updating. */
    public synchronized UnoQRobotState copy() {
        UnoQRobotState c = new UnoQRobotState();
        c.seq = seq; c.xIn = xIn; c.yIn = yIn; c.headingRad = headingRad;
        c.vxInPerS = vxInPerS; c.vyInPerS = vyInPerS; c.omegaRadPerS = omegaRadPerS;
        c.opMode = opMode; c.flags = flags; c.ackFireSeq = ackFireSeq;
        c.turretDeg = turretDeg; c.turretTargetDeg = turretTargetDeg; c.launcherRpm = launcherRpm;
        c.launcherCommandRpm = launcherCommandRpm;
        c.shots = shots; c.refused = refused;
        return c;
    }

    static short clamp16(double v) {
        long r = Math.round(v);
        return (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, r));
    }
}
