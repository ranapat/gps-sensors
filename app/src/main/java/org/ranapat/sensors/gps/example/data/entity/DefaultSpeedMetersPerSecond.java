package org.ranapat.sensors.gps.example.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "default_speed_meters_per_second",
        primaryKeys = {"gender", "activity_type"},
        indices = {
                @Index(value = {"gender"}),
                @Index(value = {"activity_type"})
        })
public class DefaultSpeedMetersPerSecond implements DataEntity {
    @NotNull
    @ColumnInfo(name = "gender")
    public final User.Gender gender;

    @NotNull
    @ColumnInfo(name = "activity_type")
    public final Session.ActivityType activityType;

    @ColumnInfo(name = "value")
    public final float value;

    public DefaultSpeedMetersPerSecond(
            final User.Gender gender,
            final Session.ActivityType activityType,
            final float value
    ) {
        this.gender = gender;
        this.activityType = activityType;
        this.value = value;
    }

}
