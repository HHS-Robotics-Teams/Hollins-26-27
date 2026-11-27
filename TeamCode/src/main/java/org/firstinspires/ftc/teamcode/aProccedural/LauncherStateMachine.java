package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.conveyor;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.counterRoller;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.intakeFront;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.launcher;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.intake_Power;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launch_Threshold;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Idle_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_Launching_Velocity;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kD;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kF;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kI;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.launcher_kP;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Power;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.volley1_Duration;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.volley_Pause_Duration;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.volley2_Duration;
import static java.lang.Math.abs;

/**
 * State Machine to control Launcher, Turret, Intake, and Feed Rollers.
 * Supports a 2-volley launch sequence (Spin Up -> Volley 1 -> Pause -> Volley 2 -> Idle).
 * Strictly verifies launcher velocity before feeding each volley to prevent undershooting.
 */
public class LauncherStateMachine {

    public enum State {
        IDLE,
        INTAKE,
        SPIN_UP,
        LAUNCHING_ONE,
        PAUSE_BETWEEN_VOLLEYS,
        LAUNCHING_TWO
    }

    private State currentState = State.IDLE;
    private final ElapsedTime stateTimer = new ElapsedTime();
    private final PIDFController launcherPIDF;

    private int turretTargetPosition = 0;
    private boolean intakeReversed = false;
    private double targetVelocity;

    public LauncherStateMachine() {
        targetVelocity = launcher_Idle_Velocity;
        stateTimer.reset();
        launcherPIDF = new PIDFController(launcher_kP, launcher_kI, launcher_kD, launcher_kF);
    }

    /**
     * Call inside loop() to update outputs and progress state transitions.
     */
    public void update() {
        // Keep PIDF coefficients updated in case modified via tuning / Dashboard
        double[] consts = launcherPIDF.getConstants();
        double[] updatedConsts = new double[]{launcher_kP, launcher_kI, launcher_kD, launcher_kF};
        for (int x = 0; x < 4; x++) {
            if (consts[x] != updatedConsts[x]) {
                launcherPIDF.setPIDF(launcher_kP, launcher_kI, launcher_kD, launcher_kF);
                break;
            }
        }

        // Turret target and power safety checks
        if (getTurretTargetPosition() != turretTargetPosition) {
            turret.setTargetPosition(turretTargetPosition);
        }
        if (turret_Power != getTurretPower()) {
            turret.setPower(turret_Power);
        }

        // --- 1. Automatic 2-Volley Sequence State Transitions ---
        switch (currentState) {
            case SPIN_UP:
                // Auto-advance to Volley 1 STRICTLY when launcher reaches target launching velocity
                if (isSpunUp()) {
                    setState(State.LAUNCHING_ONE);
                }
                break;

            case LAUNCHING_ONE:
                // Auto-advance to pause after Volley 1 duration
                if (stateTimer.seconds() >= volley1_Duration) {
                    setState(State.PAUSE_BETWEEN_VOLLEYS);
                }
                break;

            case PAUSE_BETWEEN_VOLLEYS:
                // Auto-advance to Volley 2 STRICTLY after pause time AND flywheel is confirmed at target speed
                if (stateTimer.seconds() >= volley_Pause_Duration && isSpunUp()) {
                    setState(State.LAUNCHING_TWO);
                }
                break;

            case LAUNCHING_TWO:
                // Volley sequence complete! Return to IDLE
                if (stateTimer.seconds() >= volley2_Duration) {
                    setState(State.IDLE);
                }
                break;

            default:
                break;
        }

        // --- 2. Hardware Outputs Executed Every Frame for Active State ---
        switch (currentState) {
            case IDLE:
                targetVelocity = launcher_Idle_Velocity;
                intakeFront.setPower(0);
                conveyor.setPower(0);
                if (counterRoller != null) counterRoller.setPower(0);
                break;

            case INTAKE:
                targetVelocity = launcher_Idle_Velocity;
                double dir = intakeReversed ? -1.0 : 1.0;
                intakeFront.setPower(dir * intake_Power);
                conveyor.setPower(intakeReversed ? (dir * intake_Power) : 0);
                if (counterRoller != null) counterRoller.setPower(0);
                break;

            case SPIN_UP:
                targetVelocity = launcher_Launching_Velocity;
                intakeFront.setPower(0);
                conveyor.setPower(0);
                if (counterRoller != null) counterRoller.setPower(1.0);
                break;

            case LAUNCHING_ONE:
                targetVelocity = launcher_Launching_Velocity;
                if (counterRoller != null) counterRoller.setPower(1.0);
                // Only feed if flywheel velocity is actively at target speed; pause feeding if velocity drops
                if (isSpunUp()) {
                    intakeFront.setPower(intake_Power);
                    conveyor.setPower(intake_Power);
                } else {
                    intakeFront.setPower(0);
                    conveyor.setPower(0);
                }
                break;

            case PAUSE_BETWEEN_VOLLEYS:
                targetVelocity = launcher_Launching_Velocity;
                intakeFront.setPower(0);
                conveyor.setPower(0);
                if (counterRoller != null) counterRoller.setPower(1.0);
                break;

            case LAUNCHING_TWO:
                targetVelocity = launcher_Launching_Velocity;
                if (counterRoller != null) counterRoller.setPower(1.0);
                // Only feed if flywheel velocity is actively at target speed; pause feeding if velocity drops
                if (isSpunUp()) {
                    intakeFront.setPower(intake_Power);
                    conveyor.setPower(intake_Power);
                } else {
                    intakeFront.setPower(0);
                    conveyor.setPower(0);
                }
                break;
        }

        // --- 3. Custom PIDF Control Loop output to flywheel motor power ---
        double power = launcherPIDF.calculate(targetVelocity, launcher.getVelocity());
        launcher.setPower(power);
    }

    public PIDFController getPIDFController() {
        return launcherPIDF;
    }

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

    // --- State Transition Helpers ---

    public void idle() {
        // Only return to idle if a volley sequence isn't actively executing
        if (!isVolleySequenceActive()) {
            setState(State.IDLE);
        }
    }

    public void intake() {
        // Intake mode (overridden if a volley sequence is active)
        if (!isVolleySequenceActive()) {
            setState(State.INTAKE);
        }
    }

    /**
     * Triggers the 2-volley launch sequence on a single button press.
     */
    public void startVolleySequence() {
        setState(State.SPIN_UP);
    }

    public void spinUp() {
        startVolleySequence();
    }

    public void launch() {
        startVolleySequence();
    }

    public boolean isVolleySequenceActive() {
        return currentState == State.SPIN_UP ||
               currentState == State.LAUNCHING_ONE ||
               currentState == State.PAUSE_BETWEEN_VOLLEYS ||
               currentState == State.LAUNCHING_TWO;
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

    public boolean isSpunUp() {
        return abs(launcher.getVelocity() - launcher_Launching_Velocity) <= launch_Threshold;
    }
}
