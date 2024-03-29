package org.techtown.foodtruck.DO;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Truck implements Serializable{

    private String id;
    private String image;
    private Location location;
    private String name;
    private String open_date;
    private String order_count;
    private String rate;
    private String title;
    private String type;
    private String wait_time;
    private String vendor_status;
    private double distance;


    public Truck() {
    }

    public Truck(String id, String image, Location location, String name, String open_date, String order_count, String rate, String title, String type, String wait_time, String vendor_status, double distance) {
        this.id = id;
        this.image = image;
        this.location = location;
        this.name = name;
        this.open_date = open_date;
        this.order_count = order_count;
        this.rate = rate;
        this.title = title;
        this.type = type;
        this.wait_time = wait_time;
        this.vendor_status = vendor_status;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpen_date() {
        return open_date;
    }

    public void setOpen_date(String open_date) {
        this.open_date = open_date;
    }

    public String getOrder_count() {
        return order_count;
    }

    public void setOrder_count(String order_count) {
        this.order_count = order_count;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getWait_time() {
        return wait_time;
    }

    public void setWait_time(String wait_time) {
        this.wait_time = wait_time;
    }

    public String getVendor_status() {
        return vendor_status;
    }

    public void setVendor_status(String vendor_status) {
        this.vendor_status = vendor_status;
    }

    @NonNull
    @Override
    public String toString() {
        return id+"  "+image+"  "+location+"  "+name+"  "+rate+"  "+title+"  "+type;
    }
}
