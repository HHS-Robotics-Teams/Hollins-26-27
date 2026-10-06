package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.ConveyorMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.IntakeMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherMotor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LauncherSafetyServo;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.LeftSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftArtifactCounterDistance;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightSideFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_LEVEL_TWO_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_FAR_TARGET;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_IDLE;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHER_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.SAFETY_HOLDING;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.SAFTEY_FIRING;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.aProccedural.Input;
import org.firstinspires.ftc.teamcode.aProccedural.TeleOpDrive;

@TeleOp
public class outreachBotDrive extends OpMode {
    enum Launch {
        Spin_up,
        LAUNCH,
        Reset
    }
    private Launch launch;
    Launch State = Launch.Reset;
    Input input = new Input();
    ElapsedTime intakeTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);
    ElapsedTime launchTimer = new ElapsedTime(ElapsedTime.SECOND_IN_NANO);


    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        State = Launch.Reset;
    }
    @Override
    public void start() {
        LauncherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        LauncherMotor.setPower(.2);
        LauncherSafetyServo.setPosition(SAFETY_HOLDING);
        INTAKE_RUN = false;
        INTAKE_LEVEL_TWO_RUN = false;
        LAUNCHER_RUN = false;
        intakeTimer.reset();
    }
    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        /* ---------- Drivetrain ---------- */
        TeleOpDrive.run(-gamepad1.left_stick_y,gamepad1.left_stick_x * 1.1, gamepad1.right_stick_x);

        if(input.right_trigger.held()){
            LAUNCHER_RUN = true;
            INTAKE_RUN = true;
            INTAKE_LEVEL_TWO_RUN = true;
            INTAKE_REVERSED = false;
        }
        if (LAUNCHER_RUN){
            switch (State) {
                case Spin_up:
                    LauncherMotor.setPower(.8);
                    intakeTimer.reset();
                    State = Launch.LAUNCH;
                    break;
                case LAUNCH:
                    if (LauncherMotor.getPower()>= .75){
                    LauncherSafetyServo.setPosition(SAFTEY_FIRING);
                    LeftSideFeedRoller.setPower(1);
                    rightSideFeedRoller.setPower(1);
                    launchTimer.reset();
                    State = Launch.Reset;
                    break;
                    }
                case Reset:
                    if (launchTimer.seconds()>= 3){
                        LauncherMotor.setPower(.2);
                        LauncherSafetyServo.setPosition(SAFETY_HOLDING);
                        LeftSideFeedRoller.setPower(0);
                        rightSideFeedRoller.setPower(0);
                        LAUNCHER_RUN = false;
                        INTAKE_RUN = false;
                        INTAKE_LEVEL_TWO_RUN = false;
                        State = Launch.Reset;
                        break;
                    }
            }
        }

        /* ---------- Intake ---------- */
        if (input.b.down() || input.circle.down()) {
            // Reverses intake
            INTAKE_REVERSED = !INTAKE_REVERSED;
        }
        if (input.left_trigger.held()){
            INTAKE_RUN = true;
        }
        if (input.left_trigger.up()){
            INTAKE_RUN = false;
        }
        if (input.left_bumper.held()){
            INTAKE_LEVEL_TWO_RUN = true;
        }
        if (input.left_bumper.up()){
            INTAKE_LEVEL_TWO_RUN = false;
        }


        // Control the main intake motor
        if (INTAKE_RUN) {
            IntakeMotor.setPower(INTAKE_REVERSED ? -INTAKE_POWER : INTAKE_POWER);
        } else {
            IntakeMotor.setPower(0);
        }

        if (INTAKE_LEVEL_TWO_RUN) {
            ConveyorMotor.setPower(INTAKE_REVERSED ? -1 : 1);
        } else {
            ConveyorMotor.setPower(0);
        }



        /* ---------- Telemetry ---------- */
        telemetry.addLine("--------- Comp Drive Running ---------");
        telemetry.addData("Intake running? ", INTAKE_RUN);
        telemetry.addData("Intake reversed? ", INTAKE_REVERSED);
        telemetry.addData("Launcher running? ", LAUNCHER_RUN);
    }

    }

