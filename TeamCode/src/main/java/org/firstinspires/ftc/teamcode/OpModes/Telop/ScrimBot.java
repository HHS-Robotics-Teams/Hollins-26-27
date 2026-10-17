package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.backLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.kicker;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_FEED_ROLLERS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHINGSTEP;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.apply_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_Power;

import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launching_Power;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Backwards_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Forward_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Left_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Min_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Right_Pos;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Util.TeleOpDrive;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp (name = "Scrim_Drive")
public class ScrimBot extends OpMode {

    private Input input;
    Boolean LAUNCHING;
    ElapsedTime launchingTime = new ElapsedTime();
    ElapsedTime IntakeTime = new ElapsedTime();


    @Override
    public void init() {

        LAUNCHING = false;

        Components.initComponents(hardwareMap);
//        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        turret.setTargetPosition(turret_Min_Pos);


        input = new Input();

    }

    @Override
    public void loop() {

        input.pollGamepad(gamepad1);

        double forward = gamepad1.left_stick_y;  //x
        double strafes = -gamepad1.left_stick_x;  //y
        double rotates = -(gamepad1.right_stick_x * 1); //rx

        //Setting Powers
        frontLeft.setPower(forward + strafes + rotates);
        frontRight.setPower(forward - strafes - rotates);
        backLeft.setPower(forward - strafes + rotates);
        backRight.setPower(forward + strafes - rotates);

        //Launching
        if (input.right_trigger.down()) {
            LAUNCHING = true;

        }
//        if (input.right_trigger.up()) {
//            //LAUNCHINGSTEP = 1;
//            LAUNCHING = false;
//        }
        if (LAUNCHING) {
            switch (LAUNCHINGSTEP) {
                case (1):
                    launchingTime.reset();
                    launcher.setPower(1);
                    LAUNCHINGSTEP = 2;
                    break;
                case (2):
                    if (launchingTime.seconds() > 1) {
                        IntakeTime.reset();
                        leftFeedRoller.setPower(1);
                        rightFeedRoller.setPower(1);
                        kicker.setPower(1);
                        //launcher.setPower(.5);
                        //LAUNCHING = false;
                        LAUNCHINGSTEP = 3;
                        break;
                    }
                case (3):
                    if (IntakeTime.seconds() > 1) {
                        leftFeedRoller.setPower(0);
                        rightFeedRoller.setPower(0);
                        kicker.setPower(0);
                        launcher.setPower(.5);
                        LAUNCHINGSTEP = 1;
                        LAUNCHING = false;
                        break;
                    }

            }
        }



//        // Turret Controls
//        if(input.dpad_up.down()){
//            turret.setTargetPosition(turret_Forward_Pos);
//        }
//        if(input.dpad_left.down()){
//            turret.setTargetPosition(turret_Left_Pos);
//        }
//        if(input.dpad_right.down()){
//            turret.setTargetPosition(turret_Right_Pos);
//        }
//        if(input.dpad_down.down()){
//            turret.setTargetPosition(turret_Backwards_Pos);
//        }

            //intake
            if (input.left_trigger.down()) {
                intakeFront.setPower(1);
                //INTAKE_RUN = true;
            }
            if (input.left_trigger.up()) {
                intakeFront.setPower(0);
                //INTAKE_RUN = true;
            }
//        if (input.circle.down()) {
//            // Reverses intake
//            INTAKE_REVERSED = !INTAKE_REVERSED;
//        }

            // Control the main intake motor
//        if (INTAKE_RUN) {
//            intakeFront.setPower(INTAKE_REVERSED ? -intake_Power : intake_Power);
//        } else {
//            intakeFront.setPower(0);
//        }
//
//        if (INTAKE_FEED_ROLLERS) {
//            rightFeedRoller.setPower(INTAKE_REVERSED ? -1 : 1);
//            leftFeedRoller.setPower(INTAKE_REVERSED ? -1 : 1);
//            kicker.setPower(INTAKE_REVERSED ? -1 : 1);
//        } else {
//            rightFeedRoller.setPower(0);
//            leftFeedRoller.setPower(0);
//            kicker.setPower(0);
//        }
        telemetry.addData("LaunchTime", launchingTime);
        telemetry.addData("intake time", IntakeTime);
        telemetry.addData("Launcher", launcher.getPower());
        telemetry.addData("Launching", LAUNCHING ? "True" : "False");
            telemetry.addData("state", LAUNCHINGSTEP);

        telemetry.update();
    }

    }

