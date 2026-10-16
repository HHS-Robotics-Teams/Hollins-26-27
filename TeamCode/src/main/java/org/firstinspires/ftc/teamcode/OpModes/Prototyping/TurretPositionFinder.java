package org.firstinspires.ftc.teamcode.OpModes.Prototyping;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Min_Pos;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.openftc.easyopencv.OpenCvCameraBase;

@TeleOp (name = "Position Finder", group = "Testing")
public class TurretPositionFinder extends OpMode {

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setTargetPosition(turret_Min_Pos);

    }

    @Override
    public void loop() {
        telemetry.addData("turret pos",turret.getCurrentPosition());

    }
}
