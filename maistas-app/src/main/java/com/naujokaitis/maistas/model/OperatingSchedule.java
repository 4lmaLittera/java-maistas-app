package com.naujokaitis.maistas.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.EnumType;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

@Embeddable
@Getter
public class OperatingSchedule {

    @ElementCollection
    @CollectionTable(name = "restaurant_operating_hours", joinColumns = @JoinColumn(name = "restaurant_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day_of_week")
    private Map<DayOfWeek, TimeRange> hours = new EnumMap<>(DayOfWeek.class);

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