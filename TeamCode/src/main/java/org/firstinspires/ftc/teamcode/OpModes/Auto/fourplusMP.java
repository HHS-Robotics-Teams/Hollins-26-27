package org.firstinspires.ftc.teamcode.OpModes.Auto;

import static org.firstinspires.ftc.teamcode.aProccedural.Constants.reset_Motors;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.spinUpTime;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.aProccedural.Components;
import org.firstinspires.ftc.teamcode.aProccedural.LauncherStateMachine;
import org.firstinspires.ftc.teamcode.aProccedural.TurretStateMachine;

@Autonomous (name = "Garden Side 4+Move+Park")
public class fourplusMP extends OpMode {
    private LauncherStateMachine launcherSM;
    private TurretStateMachine turretSM;
    double timeAtStart;

    @Override
    public void init() {
        Components.initComponents(hardwareMap);
        reset_Motors();
        launcherSM = new LauncherStateMachine();
        turretSM = new TurretStateMachine();
    }
    @Override
    public void start() {
        launcherSM.setState(LauncherStateMachine.State.SPIN_UP);
        if (!launcherSM.isSpunUp()) {
            launcherSM.spinUp();
            spinUpTime.reset();
        } else {
            launcherSM.launch();
        }


    }
    @Override
    public void loop() {


    }
}
