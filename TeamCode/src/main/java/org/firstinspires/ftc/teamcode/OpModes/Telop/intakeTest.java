package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.spindexer;

import androidx.core.view.KeyEventDispatcher;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.internal.camera.delegating.DelegatingCaptureSequence;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "Intake_Test")
public class intakeTest extends OpMode {

    // Variables
    // Setup
    private Input input;

    // Intake
    private Boolean intakeReverse;
    private Boolean INTAKE_RUN;

    @Override
    public void init() {
        // Setup
        Components.initComponents(hardwareMap);
        input = new Input();
    }

    @Override
    public void loop() {
        // Setup
        input.pollGamepad(gamepad1);

        // Intake
        intakeReverse = input.a.held();
        telemetry.addData("Intake Direction", intakeReverse ? "Reverse" : "Forward");

        // Logic
        if(input.left_trigger.down() && !intakeReverse) {
            setAllIntake(1);
        }
        else if(input.left_trigger.down() && intakeReverse){
            setAllIntake(-1);
        }
        if(input.left_trigger.up()){
            setAllIntake(0);
            telemetry.addData("Intake", "Off");
        }

        // Telemetry
        if(input.left_trigger.down()){
            telemetry.addData("Intake", intakeReverse ? "Reverse" : "Forward");
        }
        telemetry.addData("Intake Power",intakeFront.getPower());
        telemetry.addData("intake Running", INTAKE_RUN);

        // Output
        telemetry.update();
    }

    // Intake
    public static void setAllIntake(int speed){
        intakeFront.setPower(speed);
    }
}
