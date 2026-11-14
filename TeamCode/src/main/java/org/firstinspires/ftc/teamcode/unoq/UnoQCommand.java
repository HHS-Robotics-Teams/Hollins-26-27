package org.firstinspires.ftc.teamcode.unoq;

/**
 * UNO Q -> robot command block (protocol 2, practice only): a turret angle target and fire requests.
 * Kept small on purpose (8 bytes, one short I2C read). Layout at register 0x74, little-endian:
 *
 *   0x74 version u8 (2) | 0x75 flags u8 | 0x76 turret_target_cdeg i16 | 0x78 fire_seq u8
 *   0x79 cmd_seq u8 | 0x7A age_10ms u8 (since the UNO Q last updated it, 255 = 2.55 s or more)
 *   0x7B checksum u8 (sum of 0x74..0x7A)
 *
 * fire_seq goes up by one for each fire request; the robot acts on a CHANGE, at most once per value.
 * Keep in step with the UNO Q sketch (docs/plan-tag-sightings.md in the UNO Q repo).
 */
public class UnoQCommand {
    public static final int VERSION = 2;
    public static final int FLAG_TURRET_VALID = 0x01;  // turret_target is set
    public static final int FLAG_CAMERA_ON_UNOQ = 0x02; // the UNO Q has the camera (and is aiming)
    public static final int FLAG_LINUX_OK = 0x04;      // the UNO Q's Linux side is updating the command

    public final byte[] raw;
    public final double readMs;
    public int version, flags, fireSeq, cmdSeq, ageMs, checksum, computedChecksum;
    public double turretTargetDeg;

    UnoQCommand(byte[] raw, double readMs) {
        this.raw = raw;
        this.readMs = readMs;
        if (raw.length < UnoQ.COMMAND_LENGTH) return;
        version = raw[0] & 0xFF;
        flags = raw[1] & 0xFF;
        turretTargetDeg = (short) ((raw[2] & 0xFF) | (raw[3] << 8)) / 100.0;
        fireSeq = raw[4] & 0xFF;
        cmdSeq = raw[5] & 0xFF;
        ageMs = (raw[6] & 0xFF) * 10;
        checksum = raw[7] & 0xFF;
        int sum = 0;
        for (int i = 0; i < 7; i++) sum += raw[i] & 0xFF;
        computedChecksum = sum & 0xFF;
    }

    /** Version and checksum match: the bytes can be trusted. */
    public boolean isValid() {
        return raw.length >= UnoQ.COMMAND_LENGTH && version == VERSION && checksum == computedChecksum;
    }

    /** Valid and updated by the UNO Q's Linux side within maxAgeMs. */
    public boolean isFresh(int maxAgeMs) {
        return isValid() && flag(FLAG_LINUX_OK) && ageMs <= maxAgeMs;
    }

    public boolean flag(int bit) {
        return (flags & bit) != 0;
    }
}
