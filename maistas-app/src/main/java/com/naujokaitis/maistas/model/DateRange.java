package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class DateRange {

    private final LocalDateTime start;
    private final LocalDateTime end;

    public DateRange(LocalDateTime start, LocalDateTime end) {
        this.start = Objects.requireNonNull(start, "start must not be null");
        this.end = Objects.requireNonNull(end, "end must not be null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End must not be before start");
        }
    }

    public boolean contains(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return (dateTime.isEqual(start) || dateTime.isAfter(start)) &&
               (dateTime.isEqual(end) || dateTime.isBefore(end));
    }
}

