package org.firstinspires.ftc.teamcode.robots.nerdvana;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.field.Mechanisms;

/**
 * nerdvana's AUTO launch and intake, using the same hardware names and directions as the team's
 * aProccedural/Components.java (drive motors are left to MecanumDrive). Timings are guesses: tune in Dashboard.
 */
@Config
public final class NerdMechanisms implements Mechanisms {
    public static double LAUNCH_POWER = 1.0; // Constants.launching_Power
    public static double SPIN_UP_SECONDS = 1.0;
    public static double FEED_SECONDS = 2.0;
    public static double INTAKE_POWER = 1.0;
    public static double INTAKE_SECONDS = 1.5;
    public static double TURRET_POWER = 0.5;
    // TODO: measure (TurretPositionFinder), then set NerdRoute launchArc; 0 keeps the turret forward
    public static double TURRET_TICKS_PER_DEGREE = 0;

    private final DcMotor launcher, intakeFront, turret;
    private final CRServo leftRoller, rightRoller, kicker;

    public NerdMechanisms(HardwareMap hardwareMap) {
        launcher = hardwareMap.get(DcMotor.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeFront = hardwareMap.get(DcMotor.class, "intakeFront");
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setDirection(DcMotorSimple.Direction.REVERSE);

        // turret zero is wherever it points at init, as in ScrimBot; no power until the first launch
        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turret.setDirection(DcMotorSimple.Direction.REVERSE);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftRoller = hardwareMap.get(CRServo.class, "leftRoller");
        rightRoller = hardwareMap.get(CRServo.class, "rightRoller");
        kicker = hardwareMap.get(CRServo.class, "kicker");
        leftRoller.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public Action launch(double aimDegrees) {
        return new SequentialAction(
                new InstantAction(() -> {
                    turret.setTargetPosition((int) Math.round(aimDegrees * TURRET_TICKS_PER_DEGREE));
                    turret.setPower(TURRET_POWER);
                    launcher.setPower(LAUNCH_POWER);
                }),
                new SleepAction(SPIN_UP_SECONDS),
                new InstantAction(() -> feed(1)),
                new SleepAction(FEED_SECONDS),
                new InstantAction(() -> {
                    feed(0);
                    launcher.setPower(0);
                }));
    }

    @Override
    public Action intake(int side) {
        return new SequentialAction(
                new InstantAction(() -> intakeFront.setPower(INTAKE_POWER)),
                new SleepAction(INTAKE_SECONDS),
                new InstantAction(() -> intakeFront.setPower(0)));
    }

    @Override
    public String describe() {
        return "NerdMechanisms";
    }

    private void feed(double power) {
        intakeFront.setPower(power * INTAKE_POWER);
        leftRoller.setPower(power);
        rightRoller.setPower(power);
        kicker.setPower(power);
    }
}
