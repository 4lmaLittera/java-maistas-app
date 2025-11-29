package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.time.LocalTime;
import java.util.Objects;

@Getter
public class TimeRange {

    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimeRange(LocalTime startTime, LocalTime endTime) {
        Objects.requireNonNull(startTime, "startTime must not be null");
        Objects.requireNonNull(endTime, "endTime must not be null");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean contains(LocalTime time) {
        if (time == null) {
            return false;
        }
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }
}
