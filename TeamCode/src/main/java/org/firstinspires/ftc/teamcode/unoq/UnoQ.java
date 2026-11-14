package org.firstinspires.ftc.teamcode.unoq;

import com.qualcomm.hardware.lynx.LynxI2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.annotations.DeviceProperties;
import com.qualcomm.robotcore.hardware.configuration.annotations.I2cDeviceType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Arduino UNO Q vision co-processor on a hub I2C port (address 0x52).
 *
 * The UNO Q's STM32 is an I2C target. Its byte layout (protocol version 1) is defined in the UNO Q
 * repo, jetskibruce/AI-AprilTag: docs/plan-tag-sightings.md, "Robot feed over I2C". Keep this class
 * and that table in step.
 *
 * Out block (UNO Q -> robot), little-endian, read with a register pointer:
 *   0x00 version u8 | 0x01 status u8 | 0x02 frame u16 | 0x04 age_ms u16 | 0x06 robot_seq echo u16
 *   0x08 tag_count u8 | 0x09 ball_count u8 | 0x0A checksum u16 (sum of bytes 0x0C..0x5B)
 *   0x0C 4 tags x 12: id u8, flags u8, x_mm i16, y_mm i16, z_mm i16, bearing_cdeg i16, dist_std_mm u16
 *   0x3C 4 balls x 8: dist_mm u16, bearing_cdeg i16, color u8, conf u8, reserved u16
 *   0x5C field pose 12: x_cin i16, y_cin i16, heading_mrad i16, age_ms u16, quality u8, reserved 3
 *   0x68 path target 12: seq u8, kind u8, x_cin i16, y_cin i16, heading_mrad i16, valid_ms u16, conf u8, res 1
 * In block (robot -> UNO Q), written at 0x80, 16 bytes:
 *   robot_seq u16, x_cin i16, y_cin i16, heading_mrad i16, vx_cin_s i16, vy_cin_s i16, omega_mrad_s i16,
 *   mode u8, ack_target_seq u8
 *
 * REV hubs read at most ~100 bytes per transaction, so the out block is read in two parts:
 * 0x00..0x5B (92 bytes, checksummed) every loop, and 0x5C..0x73 (24 bytes) when needed.
 *
 * Robot configuration: add an I2C device of type "UNO Q Vision" named "unoq" on the I2C bus it is wired to.
 */
@I2cDeviceType
@DeviceProperties(name = "UNO Q Vision", xmlTag = "UnoQVision",
        description = "Arduino UNO Q AprilTag co-processor (I2C target 0x52)")
public class UnoQ extends I2cDeviceSynchDevice<I2cDeviceSynch> {
    public static final I2cAddr ADDRESS = I2cAddr.create7bit(0x52);
    public static final int PROTOCOL_VERSION = 1;

    public static final int REG_HEADER = 0x00;
    public static final int MAIN_LENGTH = 0x5C;       // header + tags + balls (92 bytes)
    public static final int REG_EXTRA = 0x5C;
    public static final int EXTRA_LENGTH = 0x74 - 0x5C; // field pose + path target (24 bytes)
    public static final int REG_ROBOT_IN = 0x80;
    public static final int ROBOT_IN_LENGTH = 16;

    public static final int MAX_TAGS = 4;
    public static final int MAX_BALLS = 4;

    public UnoQ(I2cDeviceSynch deviceClient, boolean deviceClientIsOwned) {
        super(deviceClient, deviceClientIsOwned);
        this.deviceClient.setI2cAddress(ADDRESS);
        super.registerArmingStateCallback(false);
        this.deviceClient.engage();
    }

    @Override
    protected synchronized boolean doInitialize() {
        return true;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "Arduino UNO Q Vision";
    }

    /** Bus speed of the hub port (REV hubs only). 400 kHz halves the read time; both ends must cope. */
    public void setFastBus(boolean fast) {
        if (deviceClient instanceof LynxI2cDeviceSynch) {
            ((LynxI2cDeviceSynch) deviceClient).setBusSpeed(fast
                    ? LynxI2cDeviceSynch.BusSpeed.FAST_400K
                    : LynxI2cDeviceSynch.BusSpeed.STANDARD_100K);
        }
    }

    /** Read the main block (header, tags, balls). One I2C transaction. */
    public UnoQFrame read() {
        long t0 = System.nanoTime();
        byte[] raw = deviceClient.read(REG_HEADER, MAIN_LENGTH);
        long t1 = System.nanoTime();
        return UnoQFrame.parse(raw, (t1 - t0) / 1e6);
    }

    /** Read the extra block (field pose, path target) into an existing frame. Second I2C transaction. */
    public void readExtra(UnoQFrame frame) {
        byte[] raw = deviceClient.read(REG_EXTRA, EXTRA_LENGTH);
        frame.parseExtra(raw);
    }

    /**
     * Write the robot's state (RoadRunner field frame: inches, radians; robot-frame velocity).
     * Values are sent as fixed point: 0.01 in, milliradians, 0.01 in/s, mrad/s.
     */
    public void writeRobotState(int robotSeq, double xIn, double yIn, double headingRad,
                                double vxInPerS, double vyInPerS, double omegaRadPerS,
                                int mode, int ackTargetSeq) {
        ByteBuffer b = ByteBuffer.allocate(ROBOT_IN_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
        b.putShort((short) robotSeq);
        b.putShort(clamp16(xIn * 100));
        b.putShort(clamp16(yIn * 100));
        b.putShort(clamp16(headingRad * 1000));
        b.putShort(clamp16(vxInPerS * 100));
        b.putShort(clamp16(vyInPerS * 100));
        b.putShort(clamp16(omegaRadPerS * 1000));
        b.put((byte) mode);
        b.put((byte) ackTargetSeq);
        deviceClient.write(REG_ROBOT_IN, b.array());
    }

    private static short clamp16(double v) {
        long r = Math.round(v);
        return (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, r));
    }
}
