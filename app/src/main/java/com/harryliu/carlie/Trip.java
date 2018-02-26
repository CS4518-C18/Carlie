package com.harryliu.carlie;

/**
 * @author Haofan Zhang
 * @version 2/23/18
 */

public class Trip {
    public String uid;
    public Passenger passenger;
    String start;
    String destination;
    Boolean started = false;
    Boolean ended = false;
    Long time;

    public Trip(Passenger p, String s, String d) {
        this.passenger = p;
        this.start = s;
        this.destination = d;
        this.time = System.currentTimeMillis();
        this.uid = this.passenger.uid + "-" + this.time.toString();
    }

    public Trip() {

    }

    public void startTrip() {
        started = true;
    }

    public void endTrip() {
        ended = true;
    }
}
