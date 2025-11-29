package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

@Getter
public class OperatingSchedule {

    private final Map<DayOfWeek, TimeRange> hours = new EnumMap<>(DayOfWeek.class);

    public void setHours(DayOfWeek day, TimeRange range) {
        hours.put(day, range);
    }

    public TimeRange getHours(DayOfWeek day) {
        return hours.get(day);
    }

    public boolean isOpen(LocalDateTime dateTime) {
        TimeRange range = hours.get(dateTime.getDayOfWeek());
        return range != null && range.contains(dateTime.toLocalTime());
    }
}