package org.firstinspires.ftc.teamcode.aProccedural;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.turret;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Backwards_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Forward_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Left_Pos;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Power;
import static org.firstinspires.ftc.teamcode.aProccedural.Constants.turret_Right_Pos;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * State Machine to control and test the Turret mechanism's positioning capabilities.
 */
public class TurretStateMachine {

    public enum State {
        FORWARD,
        LEFT,
        RIGHT,
        BACKWARD,
        CUSTOM_TARGET,
        MANUAL
    }

    private State currentState = State.FORWARD;
    private int targetPosition = turret_Forward_Pos;
    private double power = turret_Power;
    private final int positionTolerance = 15;
    private double manualPower = 0.0;

    public TurretStateMachine() {
        if (turret != null) {
            turret.setTargetPosition(targetPosition);
            turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            turret.setPower(power);
        }
    }

    /**
     * Call inside OpMode loop() to update the turret motor outputs.
     */
    public void update() {
        if (turret == null) return;

        if (currentState == State.MANUAL) {
            turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            turret.setPower(manualPower);
        } else {
            if (turret.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            turret.setTargetPosition(targetPosition);
            turret.setPower(power);
        }
    }

    public void setState(State newState) {
        this.currentState = newState;
        switch (newState) {
            case FORWARD:
                setTargetPosition(turret_Forward_Pos);
                break;
            case LEFT:
                setTargetPosition(turret_Left_Pos);
                break;
            case RIGHT:
                setTargetPosition(turret_Right_Pos);
                break;
            case BACKWARD:
                setTargetPosition(turret_Backwards_Pos);
                break;
            case CUSTOM_TARGET:
            case MANUAL:
                break;
        }
    }

    public State getState() {
        return currentState;
    }

    public void setTargetPosition(int pos) {
        this.targetPosition = pos;
        if (currentState != State.MANUAL && currentState != State.FORWARD && currentState != State.LEFT
                && currentState != State.RIGHT && currentState != State.BACKWARD) {
            this.currentState = State.CUSTOM_TARGET;
        }
    }

    public void adjustTargetPosition(int delta) {
        this.currentState = State.CUSTOM_TARGET;
        this.targetPosition += delta;
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public int getCurrentPosition() {
        return turret != null ? turret.getCurrentPosition() : 0;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setManualPower(double manualPower) {
        this.currentState = State.MANUAL;
        this.manualPower = manualPower;
    }

    public boolean isAtTarget() {
        return Math.abs(getCurrentPosition() - targetPosition) <= positionTolerance;
    }

    public int getPositionError() {
        return targetPosition - getCurrentPosition();
    }

    public void resetEncoder() {
        if (turret != null) {
            turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            turret.setTargetPosition(0);
            turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            targetPosition = 0;
            currentState = State.FORWARD;
        }
    }
}
