package org.ranapat.sensors.gps.example.data.tools;

import androidx.room.TypeConverter;

import org.ranapat.sensors.gps.example.data.entity.Calories;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class Converters {
    private Converters() {}

    @TypeConverter
    public static Date fromTimestamp(final Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toTimestamp(final Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Session.ActivityType toSessionActivityType(final int activityType) {
        return Session.ActivityType.values()[activityType];
    }

    @TypeConverter
    public static int fromSessionActivityType(final Session.ActivityType activityType) {
        return activityType.ordinal();
    }

    @TypeConverter
    public static Calories.CalculationMethod toCaloriesCalculationMethod(final int calculationMethod) {
        return Calories.CalculationMethod.values()[calculationMethod];
    }

    @TypeConverter
    public static int fromCaloriesCalculationMethod(final Calories.CalculationMethod calculationMethod) {
        return calculationMethod.ordinal();
    }

    @TypeConverter
    public static User.Gender toUserGender(final int gender) {
        return User.Gender.values()[gender];
    }

    @TypeConverter
    public static int fromUserGender(final User.Gender gender) {
        return gender == null ? User.Gender.Other.ordinal() : gender.ordinal();
    }

    @TypeConverter
    public static TimeUnit toTimeUnit(final int timeUnit) {
        return TimeUnit.values()[timeUnit];
    }

    @TypeConverter
    public static int fromTimeUnit(final TimeUnit timeUnit) {
        return timeUnit.ordinal();
    }
}
