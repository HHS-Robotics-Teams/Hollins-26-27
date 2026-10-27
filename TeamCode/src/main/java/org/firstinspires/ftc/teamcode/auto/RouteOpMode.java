package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.field.Alliance;
import org.firstinspires.ftc.teamcode.field.Mechanisms;
import org.firstinspires.ftc.teamcode.field.Routes;
import org.firstinspires.ftc.teamcode.robots.Robot;

/** Runs one route for one alliance on the active robot. Created by AutoOpModes, not by hand. */
public final class RouteOpMode extends LinearOpMode {
    private final Robot robot;
    private final Routes route;
    private final Alliance alliance;

    RouteOpMode(Robot robot, Routes route, Alliance alliance) {
        this.robot = robot;
        this.route = route;
        this.alliance = alliance;
    }

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        Mechanisms mechanisms = robot.mechanisms.create(hardwareMap);
        // the route picks its start pose; put the localizer there before building
        Action action = route.route.build(start -> {
            drive.localizer.setPose(start);
            return drive.actionBuilder(start);
        }, alliance, robot.route, mechanisms);

        telemetry.addData("Robot", robot.tag + " (" + robot.configName + ")");
        telemetry.addData("Route", alliance + " " + route.displayName + " (" + route.stage + ")");
        telemetry.addData("Mechanisms", mechanisms.describe());
        telemetry.addData("Start", drive.localizer.getPose());
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(action);
    }
}
