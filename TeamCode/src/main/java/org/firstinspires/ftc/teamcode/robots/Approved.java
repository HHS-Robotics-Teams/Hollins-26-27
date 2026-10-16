package org.firstinspires.ftc.teamcode.robots;

import org.firstinspires.ftc.teamcode.field.Routes;

/** A route approved for one robot's scrimmage/competition list, with its Driver Station name. */
public final class Approved {
    public final String name;
    public final Routes route;

    private Approved(String name, Routes route) {
        this.name = name;
        this.route = route;
    }

    /** name: shown as "RED name" / "BLUE name"; usually route.displayName. */
    public static Approved approve(String name, Routes route) {
        return new Approved(name, route);
    }
}
