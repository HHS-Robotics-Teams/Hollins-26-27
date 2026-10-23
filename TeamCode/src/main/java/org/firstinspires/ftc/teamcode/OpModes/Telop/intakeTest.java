package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static android.provider.SyncStateContract.Helpers.update;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.servoFeedLeft1;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.servoFeedLeft2;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.servoFeedRight1;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.servoFeedRight2;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.camera.delegating.DelegatingCaptureSequence;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp (name = "Telop_Test")
public class intakeTest extends OpMode {
    private Input input;
    private Boolean intakeReverse;
    private Boolean INTAKE_RUN ;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        input = new Input();
    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        intakeReverse = input.a.held();
        telemetry.addData("Reverse Enabled?", intakeReverse);

        if(input.left_trigger.down() && !intakeReverse) {
            setAllIntake(1);
            telemetry.addData("Forward", "Yes");
        }else if(input.left_trigger.down() && intakeReverse){
            setAllIntake(-1);
            telemetry.addData("Reverse", "Yes");
        }
        if(input.left_trigger.up()){
            setAllIntake(0);
            telemetry.addData("No intake", "Yes");
        }

        telemetry.addData("Intake Power",intakeFront.getPower());
        telemetry.addData("intake Running", INTAKE_RUN );
        telemetry.update();
    }

    public void setAllIntake(int speed){
        intakeFront.setPower(speed);
        servoFeedLeft1.setPower(speed);
        servoFeedRight1.setPower(-speed);
        servoFeedLeft2.setPower(speed);
        servoFeedRight2.setPower(-speed);
    }
}
