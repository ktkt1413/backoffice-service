package com.dayaeyak.backofficservice.backoffice.client.enums.restaurant;

import java.time.DayOfWeek;

public enum ClosedDays {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    NONE; // 매일 영업

    public static ClosedDays fromDayOfWeek(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> MONDAY;
            case TUESDAY -> TUESDAY;
            case WEDNESDAY -> WEDNESDAY;
            case THURSDAY -> THURSDAY;
            case FRIDAY -> FRIDAY;
            case SATURDAY -> SATURDAY;
            case SUNDAY -> SUNDAY;
        };
    }
}
