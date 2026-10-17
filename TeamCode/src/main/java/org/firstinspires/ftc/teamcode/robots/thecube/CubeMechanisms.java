package org.firstinspires.ftc.teamcode.robots.thecube;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.field.Mechanisms;

/** theCube has no launcher or intake: each action pauses as long as MeepMeep assumes, so any route still runs. */
public final class CubeMechanisms implements Mechanisms {
    public CubeMechanisms(HardwareMap hardwareMap) {}

    @Override
    public Action launch(double aimDegrees) {
        return new SleepAction(CubeRoute.TUNING.simLaunchSeconds);
    }

    @Override
    public Action intake(int side) {
        return new SleepAction(CubeRoute.TUNING.simIntakeSeconds);
    }

    @Override
    public String describe() {
        return "CubeMechanisms (no-op pauses)";
    }
}
