package org.firstinspires.ftc.teamcode.Util;

import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.frontRight;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backLeft;
import static org.firstinspires.ftc.teamcode.aProccedural.Components.backRight;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class TeleOpDrive {

    @SuppressWarnings("ReassignedVariable")
    public static void run(double forward, double strafes, double rotates){
        //thresholds
        if (abs(forward) <= 0.15) {
            forward = 0;
        }
        if (abs(strafes) <= 0.15) {
            strafes = 0;
        }
        if (abs(rotates) <= 0.15) {
            rotates = 0;
        }

        //Power fixer
        double denominator = max((abs(forward) + abs(strafes) + abs(rotates)), 1);

        //Setting Powers
        frontLeft.setPower((forward + strafes + rotates) / denominator);
        frontRight.setPower((forward - strafes - rotates) / denominator);
        backLeft.setPower((forward - strafes + rotates) / denominator);
        backRight.setPower((forward + strafes - rotates) / denominator);
    }

}
