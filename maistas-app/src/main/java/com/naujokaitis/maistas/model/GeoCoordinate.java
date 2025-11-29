package com.naujokaitis.maistas.model;

import lombok.Getter;

@Getter
public class GeoCoordinate {

    private final double latitude;
    private final double longitude;

    public GeoCoordinate(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

