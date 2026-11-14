package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Drawing;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.robots.Robot;

/**
 * Basic robot-centric mecanum drive for the active robot. Created by TeleOpModes, not by hand.
 * Motor directions and IMU orientation come from the robot profile (robots/<robot>/<Robot>.java) via MecanumDrive.
 *
 *   left stick:   drive / strafe
 *   right stick:  turn
 *   left bumper:  hold for slow mode
 */
@Config
public final class DriveOpMode extends LinearOpMode {
    /** Stick values below this are treated as zero. */
    public static double DEADBAND = 0.05;
    /** Power scale while slow mode is held. */
    public static double SLOW_SCALE = 0.35;

    private final Robot robot;

    DriveOpMode(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        telemetry.addData("Robot", robot.tag + " (" + robot.configName + ")");
        telemetry.addLine("Left stick: drive  |  Right stick: turn  |  LB: slow");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double scale = gamepad1.left_bumper ? SLOW_SCALE : 1.0;

            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(
                            deadband(-gamepad1.left_stick_y) * scale,
                            deadband(-gamepad1.left_stick_x) * scale
                    ),
                    deadband(-gamepad1.right_stick_x) * scale
            ));

            drive.updatePoseEstimate();
            Pose2d pose = drive.localizer.getPose();

            telemetry.addData("Robot", robot.tag);
            telemetry.addData("Slow mode", gamepad1.left_bumper);
            telemetry.addData("x", pose.position.x);
            telemetry.addData("y", pose.position.y);
            telemetry.addData("heading (deg)", Math.toDegrees(pose.heading.toDouble()));
            telemetry.update();

            TelemetryPacket packet = new TelemetryPacket();
            packet.fieldOverlay().setStroke("#3F51B5");
            Drawing.drawRobot(packet.fieldOverlay(), pose);
            FtcDashboard.getInstance().sendTelemetryPacket(packet);
        }
    }

    private static double deadband(double value) {
        return Math.abs(value) < DEADBAND ? 0 : value;
    }
}
