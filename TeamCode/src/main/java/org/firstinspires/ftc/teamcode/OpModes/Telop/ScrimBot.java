package org.firstinspires.ftc.teamcode.OpModes.Telop;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.kicker;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.leftFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.rightFeedRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_FEED_ROLLERS;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_RUN;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LAUNCHING;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.apply_Powers;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_Power;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Launching_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Backwards_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Forward_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Left_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Min_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.INTAKE_REVERSED;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Right_Pos;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Util.TeleOpDrive;
import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.Input;

@TeleOp (name = "Scrim_Drive")
public class ScrimBot extends OpMode {

    private Input input;


    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setTargetPosition(turret_Min_Pos);

        apply_Powers();

        input = new Input();

    }

    @Override
    public void loop() {
        input.pollGamepad(gamepad1);

        TeleOpDrive.run(-gamepad1.left_stick_y,gamepad1.left_stick_x * 1.1, gamepad1.right_stick_x);
        //Launching
        if (input.right_trigger.held()){
            LAUNCHING = true;
        }
        if (LAUNCHING){
            launcher.setVelocity(launcher_Launching_Velocity);
            INTAKE_RUN = true;
            INTAKE_FEED_ROLLERS = true;
        }
        // Turret Controls
        if ((input.left_bumper.Held) && turret.getCurrentPosition()>= turret_Backwards_Pos){
            turret.setTargetPosition(turret.getCurrentPosition()-100);

        }
        if ((input.right_bumper.Held) && turret.getCurrentPosition()>= (turret_Backwards_Pos- 5)){
            turret.setTargetPosition(turret.getCurrentPosition() + 100);

        }
        if(input.dpad_up.down()){
            turret.setTargetPosition(turret_Forward_Pos);
        }
        if(input.dpad_left.down()){
            turret.setTargetPosition(turret_Left_Pos);
        }
        if(input.dpad_right.down()){
            turret.setTargetPosition(turret_Right_Pos);
        }
        if(input.dpad_down.down()){
            turret.setTargetPosition(turret_Backwards_Pos);
        }

        //intake
        if(input.left_trigger.held()){
            INTAKE_RUN = true;
        }

        if (input.circle.down()) {
            // Reverses intake
            INTAKE_REVERSED = !INTAKE_REVERSED;
        }

        // Control the main intake motor
        if (INTAKE_RUN) {
            intakeFront.setPower(INTAKE_REVERSED ? -intake_Power : intake_Power);
        } else {
            intakeFront.setPower(0);
        }

        if (INTAKE_FEED_ROLLERS) {
            rightFeedRoller.setPower(INTAKE_REVERSED ? -1 : 1);
            leftFeedRoller.setPower(INTAKE_REVERSED ? -1 : 1);
            kicker.setPower(INTAKE_REVERSED ? -1 : 1);
        } else {
            rightFeedRoller.setPower(0);
            leftFeedRoller.setPower(0);
            kicker.setPower(0);
        }


    }
}
