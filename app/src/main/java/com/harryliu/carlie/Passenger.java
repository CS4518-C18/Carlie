package com.harryliu.carlie;

/**
 * @author Haofan Zhang
 * @version 2/22/18
 */

public class Passenger {
    public String uid;
    public String phone;
    public String name;
    public String type;

    public Passenger (String uid, String phone, String name, String type) {
        this.uid = uid;
        this.phone = phone;
        this.name = name;
        this.type = type;
    }

    public Passenger () {

    }
}
