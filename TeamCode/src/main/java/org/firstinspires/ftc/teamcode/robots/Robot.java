package org.firstinspires.ftc.teamcode.robots;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.field.Mechanisms;
import org.firstinspires.ftc.teamcode.field.RouteTuning;

import java.util.List;

/** Everything one robot brings to the shared code. Each robot builds one in robots/<robot>/<Robot>.java. */
public final class Robot {
    public interface MechanismsFactory {
        Mechanisms create(HardwareMap hardwareMap);
    }

    /** Control Hub config name prefix that selects this robot, e.g. "nerdvana". */
    public final String configName;
    /** Short tag for class names, telemetry and test entries, e.g. "nerd". */
    public final String tag;
    public final MecanumDrive.Params drive;
    public final RouteTuning route;
    public final MechanismsFactory mechanisms;
    /** This robot's scrimmage/competition AUTO list. */
    public final List<Approved> approved;

    public Robot(String configName, String tag, MecanumDrive.Params drive, RouteTuning route,
                 MechanismsFactory mechanisms, List<Approved> approved) {
        this.configName = configName;
        this.tag = tag;
        this.drive = drive;
        this.route = route;
        this.mechanisms = mechanisms;
        this.approved = approved;
    }
}
