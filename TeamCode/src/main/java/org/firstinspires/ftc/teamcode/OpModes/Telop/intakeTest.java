package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static android.provider.SyncStateContract.Helpers.update;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.camera.delegating.DelegatingCaptureSequence;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp (name = "Telop_Test")
public class intakeTest extends OpMode {
    private Input input;
    private Boolean INTAKE_RUN ;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        input = new Input();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        if (input.left_trigger.down()) {
            intakeFront.setPower(1);
        }
        if (input.left_trigger.up()){
            intakeFront.setPower(0);
        }

        telemetry.addData("IntakePower",intakeFront.getPower());
        telemetry.addData("intake run", INTAKE_RUN );

    }
}
