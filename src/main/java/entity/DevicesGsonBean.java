package main.java.entity;

import java.util.ArrayList;

public class DevicesGsonBean {
    /*{
        "devices":[{"ip":"","mark":"","isselect":false,"isping":false}]
    }*/
    ArrayList<Device> devices;

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }
}
