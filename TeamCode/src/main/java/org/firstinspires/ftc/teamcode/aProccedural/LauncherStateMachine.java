package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.conveyor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.counterRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.LaunchingPower;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.CounterRollerPower;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.feed_Roller_Power;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_Power;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launch_Threshold;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Idle_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Launching_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kF;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kI;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kP;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.spinUpTime;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Power;
import static java.lang.Math.abs;

/**
 * State Machine to control the Launcher, Turret, Intake, and Feed Rollers using a custom PIDF loop on the launcher flywheel.
 * States: IDLE, INTAKE, AIM (WHYYYYYYYY), SPIN_UP, LAUNCHING
 */
public class LauncherStateMachine {

    public enum State {
        IDLE,
        INTAKE,
        // AIM,
        SPIN_UP,
        LAUNCHING
    }

    private State currentState = State.IDLE;
    private State lastState = currentState;
    private final ElapsedTime stateTimer = new ElapsedTime();
    private final PIDFController launcherPIDF;

    // Configuration constants
    private double spinUpTimeSeconds = 0.5; // Time required for flywheel to reach full speed
    private int turretTargetPosition = 0;
    private boolean intakeReversed = false;
    private boolean lastReversed = intakeReversed;
    private double targetVelocity;
    public LauncherStateMachine() {
        targetVelocity = launcher_Idle_Velocity;
        stateTimer.reset();
        launcherPIDF = new PIDFController(launcher_kP, launcher_kI, launcher_kD, launcher_kF);
    }

    /**
     * Call this inside your OpMode's loop() method to update mechanism outputs based on the active state.
     */
    public void update() {
        // Keep PIDF coefficients updated in case modified via tuning / Dashboard
        double[] consts = launcherPIDF.getConstants();
        double[] updatedConsts = new double[] {launcher_kP, launcher_kI, launcher_kD, launcher_kF};
        for(int x = 0; x < 4; x++) {
            if(consts[x] != updatedConsts[x]) {
                launcherPIDF.setPIDF(launcher_kP, launcher_kI, launcher_kD, launcher_kF);
                break;
            }
        }

        if (getTurretTargetPosition() != turretTargetPosition) {
            turret.setTargetPosition(turretTargetPosition);
        }
        if (turret_Power != getTurretPower()) {
            turret.setPower(turret_Power);
        }

        if(lastState != currentState || lastReversed != intakeReversed) {
            if(lastState != currentState) {
               stateTimer.reset();
            }

            switch (currentState) {
                case IDLE:
                    targetVelocity = launcher_Idle_Velocity;
                    intakeFront.setPower(0);
                    conveyor.setPower(0);
                    //kicker.setPosition(0.0);
                    break;

                case INTAKE:
                    targetVelocity = launcher_Idle_Velocity;
                    double dir = intakeReversed ? -1.0 : 1.0;
                    intakeFront.setPower(dir * intake_Power);
                    break;

                // case AIM:
                //     targetVelocity = launcher_Launching_Velocity;
                //     intakeFront.setPower(0);
                //     leftFeedRoller.setPower(0);
                //     rightFeedRoller.setPower(0);
                //     kicker.setPosition(0.0);
                //     break;

                case SPIN_UP:
                    targetVelocity = launcher_Launching_Velocity;
                    counterRoller.setPower(1);
                    //kicker.setPosition(0.0);
                    if (abs(launcher.getVelocity() - targetVelocity) <= launch_Threshold){
                        setState(State.LAUNCHING);
                    }
                    break;

                case LAUNCHING:
                    counterRoller.setPower(1);
                    //kicker.setPosition(1.0);
                    intakeFront.setPower(intake_Power);
                    conveyor.setPower(intake_Power);
                    break;
            }
        }

        // Apply custom PIDF control loop output to flywheel motor power
        double power = launcherPIDF.calculate(targetVelocity, launcher.getVelocity());
        if(abs(launcher.getPower() - power) >= 0.04 || (power == 0 && launcher.getPower() != 0)) {
            launcher.setPower(power);
        }
        lastState = currentState;
        lastReversed = intakeReversed;
    }

    private void setCounterRollerPower() {
        LaunchingPower = launcher.getPower();
        CounterRollerPower = (LaunchingPower * .95);

    }

    public PIDFController getPIDFController() {
        return launcherPIDF;
    }

    /**
     * Transition to a new state and reset the state timer.
     */
    public void setState(State newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            stateTimer.reset();
        }
    }

    public State getState() {
        return currentState;
    }

    public double getStateTimeSeconds() {
        return stateTimer.seconds();
    }

    // --- State Transition Shortcuts ---

    public void idle() {
        setState(State.IDLE);
    }

    public void intake() {
        setState(State.INTAKE);
    }

    public void aim(int targetPosition) {
        this.turretTargetPosition = targetPosition;
        spinUp();
        // setState(State.AIM);
    }

    public void spinUp() {
        setState(State.SPIN_UP);
    }

    public void launch() {
        setState(State.LAUNCHING);
    }

    // --- Turret & Intake Controls ---

    public void setTurretTargetPosition(int targetPosition) {
        this.turretTargetPosition = targetPosition;
    }

    public int getTurretTargetPosition() {
        return turretTargetPosition;
    }

    public double getTurretPower() {
        return turret.getPower();
    }

    public void setIntakeReversed(boolean reversed) {
        this.intakeReversed = reversed;
    }

    public boolean isIntakeReversed() {
        return intakeReversed;
    }

    public void toggleIntakeReversed() {
        this.intakeReversed = !this.intakeReversed;
    }

    public void setSpinUpTimeSeconds(double seconds) {
        this.spinUpTimeSeconds = seconds;
    }

    public boolean isSpunUp() {
        return (currentState == State.SPIN_UP || currentState == State.LAUNCHING)
                && (launcher.getVelocity() >= launcher_Launching_Velocity * 0.9 || stateTimer.seconds() >= spinUpTimeSeconds);
    }

}
