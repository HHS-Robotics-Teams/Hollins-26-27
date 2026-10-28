package org.firstinspires.ftc.teamcode.aProccedural;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Custom PIDF Controller with Feedforward, Integral Anti-Windup Clamping,
 * and Telemetry Diagnostics for FTC Motor Velocity & Position Control.
 */
/*
DashBoard
http://192.168.43.1:8080/dash
    Select Launcher Flywheel Tuner from the Driver Station TeleOp menu.
Gamepad Input
Action

A Button
Toggle flywheel motor ON / OFF (Step Response test: revs up from 0 to target velocity to observe acceleration and settle time)

B Button
Toggle target velocity preset between launcher_Idle_Velocity and launcher_Launching_Velocity

X Button
Reset PIDF controller history (clears integral sum)

Y Button
Save active tuned $k_P, k_I, k_D, k_F$ values into Constants.java memory

D-Pad Left / Right
Cycle parameter selection (kP $\rightarrow$ kI $\rightarrow$ kD $\rightarrow$ kF $\rightarrow$ Target Velocity)

D-Pad Up / Down
Increase / Decrease active parameter value by the current step size

LB / RB Bumpers
Increase / Decrease step size factor (0.00001, 0.0001, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0)

Flywheel Tuning Procedure
1.
Tune k_F (Feedforward) First:
◦
Set k_P, k_I, k_D = 0.
◦
Adjust k_F until motor speed roughly matches Target Velocity at steady state (k_F ≈ (1)/(Max Velocity)).
2.
Tune k_P (Proportional):
◦
Increase k_P until the flywheel rapidly recovers speed when game elements enter the launcher, but before oscillations occur.
3.
Tune k_D (Derivative):
◦
Add a small amount of k_D to dampen any overshoot or RPM ringing when spinning up.
4.
Tune k_I (Integral):
◦
Add a tiny k_I value to eliminate steady-state RPM error.
5.
Save: Press Y Button on Gamepad 1 to save the tuned parameters into Constants.
 */
public class PIDFController {
    private double kP;
    private double kI;
    private double kD;
    private double kF;

    private double target = 0;
    private double lastError = 0;
    private double integralSum = 0;
    private double maxIntegralSum = 1.0; // Anti-windup limit
    private double minOutput = -1.0;
    private double maxOutput = 1.0;

    private double pTerm = 0;
    private double iTerm = 0;
    private double dTerm = 0;
    private double fTerm = 0;

    private final ElapsedTime timer = new ElapsedTime();
    private boolean firstRun = true;

    public PIDFController(double kP, double kI, double kD, double kF) {
        setPIDF(kP, kI, kD, kF);
        timer.reset();
    }

    public void setPIDF(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double getTarget() {
        return target;
    }

    /**
     * Calculates motor power given a new target and current velocity/position.
     */
    public double calculate(double target, double current) {
        this.target = target;
        return calculate(current);
    }

    /**
     * Calculates motor power given current velocity/position and set target.
     */
    public double calculate(double current) {
        double dt = timer.seconds();
        timer.reset();

        // Guard against loop lag spikes or initial start time
        if (dt <= 0 || dt > 0.2 || firstRun) {
            dt = 0.02; // Default to estimated ~50Hz loop rate
            firstRun = false;
        }

        double error = target - current;

        // Feedforward term (proportional to target setpoint)
        fTerm = target * kF;

        // Proportional term
        pTerm = error * kP;

        // Integral term with anti-windup clamping
        if (kI != 0) {
            integralSum += error * dt;
            if (integralSum * kI > maxIntegralSum) {
                integralSum = maxIntegralSum / kI;
            } else if (integralSum * kI < -maxIntegralSum) {
                integralSum = -maxIntegralSum / kI;
            }
            iTerm = integralSum * kI;
        } else {
            integralSum = 0;
            iTerm = 0;
        }

        // Derivative term
        double derivative = (error - lastError) / dt;
        dTerm = derivative * kD;
        lastError = error;

        // Total combined output
        double output = fTerm + pTerm + iTerm + dTerm;

        // Saturate output to motor range [-1.0, 1.0]
        if (output > maxOutput) {
            output = maxOutput;
        } else if (output < minOutput) {
            output = minOutput;
        }

        return output;
    }

    public void reset() {
        integralSum = 0;
        lastError = 0;
        firstRun = true;
        timer.reset();
    }

    // Getters for live telemetry analysis
    public double getP() { return pTerm; }
    public double getI() { return iTerm; }
    public double getD() { return dTerm; }
    public double getF() { return fTerm; }
    public double getKP() { return kP; }
    public double getKI() { return kI; }
    public double getKD() { return kD; }
    public double getKF() { return kF; }
    public double getLastError() { return lastError; }
}
