package org.ranapat.sensors.gps.example.data.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "packages",
        indices = @Index(value = {"session_id"}),
        foreignKeys = @ForeignKey(
                entity = Session.class,
                parentColumns = "id",
                childColumns = "session_id",
                onDelete = CASCADE
        ))
public class Package implements DataEntity, Duplicatable<Package> {
    public static Package invalid() {
        return new Package(
                -1, null, 0, 0, null, null, null, 0.0f, false
        );
    }

    @PrimaryKey(autoGenerate = true)
    public final long id;

    @ColumnInfo(name = "session_id")
    public final Long sessionId;

    @ColumnInfo(name = "index")
    public final int index;

    @ColumnInfo(name = "interval")
    public final long interval;

    @ColumnInfo(name = "time_units")
    public final TimeUnit timeUnits;

    @ColumnInfo(name = "start")
    public final Date start;

    @ColumnInfo(name = "end")
    public final Date end;

    @ColumnInfo(name = "suspicious")
    public float suspicious;

    @ColumnInfo(name = "synced")
    public boolean synced;

    @Ignore
    public List<Location> locationsRaw;

    @Ignore
    public List<Location> locationsProcessed;

    @Ignore
    public StepSummary stepSummary;

    @Ignore
    public Calories calories;

    public Package(
            final long id,
            final Long sessionId,
            final int index,
            final long interval,
            final TimeUnit timeUnits,
            final Date start,
            final Date end,
            final float suspicious,
            final boolean synced
    ) {
        this.id = id;
        this.sessionId = sessionId;
        this.index = index;
        this.interval = interval;
        this.timeUnits = timeUnits;
        this.start = start;
        this.end = end;
        this.suspicious = suspicious;
        this.synced = synced;
    }

    @Ignore
    public Package(
            final long id,
            final Long sessionId,
            final int index,
            final long interval,
            final TimeUnit timeUnits,
            final Date start,
            final Date end,
            final float suspicious,
            final boolean synced,
            final List<Location> locationsRaw,
            final List<Location> locationsProcessed,
            final StepSummary stepSummary,
            final Calories calories
    ) {
        this(
                id,
                sessionId, index,
                interval, timeUnits,
                start,
                end,
                suspicious,
                synced
        );

        this.locationsRaw = locationsRaw;
        this.locationsProcessed = locationsProcessed;
        this.stepSummary = stepSummary;
        this.calories = calories;
    }

    @Ignore
    public Package(
            final Long sessionId,
            final int index,
            final long interval,
            final TimeUnit timeUnits,
            final Date start,
            final Date end,
            final float suspicious,
            final boolean synced,
            final List<Location> locationsRaw,
            final List<Location> locationsProcessed,
            final StepSummary stepSummary,
            final Calories calories
    ) {
        this(
                0,
                sessionId, index,
                interval, timeUnits,
                start,
                end,
                suspicious,
                synced
        );

        this.locationsRaw = locationsRaw;
        this.locationsProcessed = locationsProcessed;
        this.stepSummary = stepSummary;
        this.calories = calories;
    }

    @Override
    public Package duplicate(final Map<String, Object> parameters) {
        final long id = parameters.containsKey("id") ? (long) parameters.get("id") : this.id;
        final boolean synced = parameters.containsKey("synced") ? (boolean) parameters.get("synced") : this.synced;

        return new Package(
                id,
                this.sessionId,
                this.index,
                this.interval,
                this.timeUnits,
                this.start,
                this.end,
                this.suspicious,
                synced,
                this.locationsRaw,
                this.locationsProcessed,
                this.stepSummary,
                this.calories
        );
    }

}
