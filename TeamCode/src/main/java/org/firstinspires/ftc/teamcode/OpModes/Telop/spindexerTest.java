package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightRear;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.spindexer;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.counterRoller;


import static java.lang.Thread.sleep;

import androidx.core.view.KeyEventDispatcher;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.internal.camera.delegating.DelegatingCaptureSequence;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp(name = "Spindexer_Test")
public class spindexerTest extends OpMode {

    // Variables
    // Setup
    private Input input;

    // Spindexer
    private static double adjustSpeed = -0.75;
    private double adjustIncrement = 0.05;
    private double ticksPerRev = spindexer.getMotorType().getTicksPerRev();
    private int ticks90 = (int) Math.round(90/360*ticksPerRev);

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

        // Spindexer
        if (input.left_bumper.down()) {
            adjustSpeed -= adjustIncrement;
            telemetry.addData("Adjusted DOWN to "+adjustSpeed+" by increment ",adjustIncrement);
        }
        if (input.right_bumper.down()) {
            adjustSpeed += adjustIncrement;
            telemetry.addData("Adjusted UP to "+adjustSpeed+" by increment ",adjustIncrement);
        }

        if (input.y.down()) {
            //spinTheDexer(90);
            spindexer.setPower(.15);
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
            spinTheDexerLikeCrazy();
            telemetry.addData("x pressed? ", input.x.down());
            telemetry.addData("motor power ", spindexer.getPower());
            telemetry.update();
        }
        if (input.x.up()) {
            spindexer.setPower(0);
            telemetry.addData("x unpressed?", input.x.up());
            telemetry.addData("motor power ", spindexer.getPower());
            telemetry.update();
        }
    }

    // Spindexer
    public void spinTheDexer(int ticks) {
        spindexer.setTargetPosition(spindexer.getCurrentPosition() + ((int) ticks));
    }

    public static void spinTheDexerLikeCrazy() {
        launcher.setPower(1);
        counterRoller.setPower(1);
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        spindexer.setPower(adjustSpeed);
    }

    public void resetSpinDexer()
    {

    }
}
