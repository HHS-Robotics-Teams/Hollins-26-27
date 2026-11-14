package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.spindexer;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.counterRoller;

import androidx.core.view.KeyEventDispatcher;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.internal.camera.delegating.DelegatingCaptureSequence;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "M1_Telop")
public class M1_Telop extends OpMode {

    // Variables
    private Input input;

    // Spindexer
    private double adjustSpeed = 0.75;
    private double adjustIncrement = 0.05;
    private double ticksPer360 = 537.7;

    // Intake
    private Boolean intakeReverse;
    private Boolean INTAKE_RUN;

    @Override
    public void init() {
        // Setup
        Components.initComponents(hardwareMap);
        input = new Input();

        // Spindexer
        spindexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {
        // Setup
        input.pollGamepad(gamepad1);

        // Drivetrain
        double y= gamepad1.left_stick_y;
        double x= -gamepad1.left_stick_x;
        double rx= -gamepad1.right_stick_x;
        leftFront.setPower(y+x+rx);
        leftRear.setPower(y-x+rx);
        rightFront.setPower(y-x-rx);
        rightRear.setPower(y+x-rx);

        // Intake
        intakeReverse = input.a.held();
        telemetry.addData("Intake Direction", intakeReverse ? "Reverse" : "Forward");

        // Logic
        if(input.left_trigger.down() && !intakeReverse) {
            intakeTest.setAllIntake(1);
            spindexer.setPower(-.2);
        }
        else if(input.left_trigger.down() && intakeReverse){
            intakeTest.setAllIntake(-1);
        }
        if(input.left_trigger.up()){
            intakeTest.setAllIntake(0);
            spindexer.setPower(0);
            telemetry.addData("Intake", "Off");
        }

        // Telemetry
        if(input.left_trigger.down()){
            telemetry.addData("Intake", intakeReverse ? "Reverse" : "Forward");
        }
        telemetry.addData("Intake Power",intakeFront.getPower());
        telemetry.addData("intake Running", INTAKE_RUN);

        // Spindexer
        if (input.left_bumper.down()) {
            adjustSpeed -= adjustIncrement;
        }
        if (input.right_bumper.down()) {
            adjustSpeed += adjustIncrement;
        }

        if (input.y.down()) {
            //spinTheDexer(90);
            spindexer.setPower(-.2);
            //spindexer.setTargetPosition(spindexer.getCurrerntPosition()+50);
            telemetry.addData("y pressed?", input.y.down());
            telemetry.addData("motor power", spindexer.getPower());
            telemetry.update();
        }
        if (input.y.up()) {
            spindexer.setPower(0);
            telemetry.addData("y unpressed? ", input.y.up());
            telemetry.addData("motor power ", spindexer.getPower());
            telemetry.update();
        }
        if (input.x.down()) {
            spindexerTest.spinTheDexerLikeCrazy();
            telemetry.addData("x pressed? ", input.x.down());
            telemetry.addData("motor power ", spindexer.getPower());
            telemetry.update();
        }
        if (input.x.up()) {
            spindexer.setPower(0);
            launcher.setPower(0);
            counterRoller.setPower(0);
            telemetry.addData("x unpressed?", input.x.up());
            telemetry.addData("motor power ", spindexer.getPower());
            telemetry.update();
        }
    }
}
