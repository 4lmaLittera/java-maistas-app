package com.naujokaitis.maistas.api.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeRange {

    private LocalTime startTime;
    private LocalTime endTime;

    public boolean contains(LocalTime time) {
        if (time == null) {
            return false;
        }
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }
}
