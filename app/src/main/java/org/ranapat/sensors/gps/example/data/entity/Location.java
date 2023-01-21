package org.ranapat.sensors.gps.example.data.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.ranapat.sensors.gps.example.Settings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "locations",
        indices = @Index(value = {"package_id"}),
        foreignKeys = @ForeignKey(
                entity = Package.class,
                parentColumns = "id",
                childColumns = "package_id",
                onDelete = CASCADE
        ))
public class Location implements DataEntity, Duplicatable<Location> {
    @PrimaryKey(autoGenerate = true)
    public final long id;

    @ColumnInfo(name = "package_id")
    public final Long packageId;

    @ColumnInfo(name = "index")
    public final int index;

    @ColumnInfo(name = "latitude")
    public final double latitude;

    @ColumnInfo(name = "longitude")
    public final double longitude;

    @ColumnInfo(name = "altitude")
    public final double altitude;

    @ColumnInfo(name = "time")
    public final Date time;

    @ColumnInfo(name = "accuracy")
    public final float accuracy;

    @ColumnInfo(name = "method")
    public final String method;

    @ColumnInfo(name = "processed")
    public final String processed;

    @ColumnInfo(name = "suspicious")
    public float suspicious;

    public Location(
            final long id,
            final Long packageId,
            final int index,
            final double latitude,
            final double longitude,
            final double altitude,
            final Date time,
            final float accuracy,
            final String method,
            final String processed,
            final float suspicious
    ) {
        this.id = id;
        this.packageId = packageId;
        this.index = index;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.time = time;
        this.accuracy = accuracy;
        this.method = method;
        this.processed = processed;
        this.suspicious = suspicious;
    }

    @Ignore
    public Location(
            final int index,
            final double latitude,
            final double longitude,
            final double altitude,
            final Date time,
            final float accuracy,
            final String method,
            final String processed,
            final float suspicious
    ) {
        this(
                0, null,
                index,
                latitude, longitude, altitude,
                time, accuracy,
                method, processed,
                suspicious
        );
    }

    @Override
    public Location duplicate(final Map<String, Object> parameters) {
        final long id = parameters.containsKey("id") ? (long) parameters.get("id") : this.id;
        final Long packageId = parameters.containsKey("packageId") ? (Long) parameters.get("packageId") : this.packageId;
        final Double latitude = parameters.containsKey("latitude") ? (Double) parameters.get("latitude") : this.latitude;
        final Double longitude = parameters.containsKey("longitude") ? (Double) parameters.get("longitude") : this.longitude;
        final Double altitude = parameters.containsKey("altitude") ? (Double) parameters.get("altitude") : this.altitude;
        final Float accuracy = parameters.containsKey("accuracy") ? (Float) parameters.get("accuracy") : this.accuracy;

        return new Location(
                id,
                packageId,
                this.index,
                latitude,
                longitude,
                altitude,
                this.time,
                accuracy,
                this.method,
                this.processed,
                this.suspicious
        );
    }

    public Location accuracyToPercentage() {
        final float normalized = Math.min(
                Settings.locationAccuracyWorst,
                Math.max(Settings.locationAccuracyBest, accuracy)
        );
        final float percentage = 1 - (normalized - Settings.locationAccuracyBest) / (Settings.locationAccuracyWorst - Settings.locationAccuracyBest);

        return duplicate(new HashMap<String, Object>() {{
            put("accuracy", percentage);
        }});
    }

}
