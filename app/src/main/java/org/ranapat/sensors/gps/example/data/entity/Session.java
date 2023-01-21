package org.ranapat.sensors.gps.example.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Map;

@Entity(tableName = "sessions",
        indices = @Index(value = {"session_id"}, unique = true))
public class Session implements DataEntity, Duplicatable<Session> {
    public enum ActivityType {
        Undefined,
        Standing,
        Walking,
        Jogging,
        Sprinting
    }

    public static Session invalid() {
        return new Session(
                -1, null, null, null, null, null, false, false, null
        );
    }

    @PrimaryKey(autoGenerate = true)
    public final long id;

    @ColumnInfo(name = "session_id")
    public final String sessionId;

    @ColumnInfo(name = "activity_type")
    public final Session.ActivityType activityType;

    @ColumnInfo(name = "calories_calculation_method")
    public final Calories.CalculationMethod caloriesCalculationMethod;

    @ColumnInfo(name = "created_at")
    public final Date createdAt;

    @ColumnInfo(name = "closed_at")
    public Date closedAt;

    @ColumnInfo(name = "active")
    public boolean active;

    @ColumnInfo(name = "create_synced")
    public boolean createSynced;

    @ColumnInfo(name = "close_synced")
    public Boolean closeSynced;

    public Session(
            final long id,
            final String sessionId,
            final Session.ActivityType activityType,
            final Calories.CalculationMethod caloriesCalculationMethod,
            final Date createdAt,
            final Date closedAt,
            final boolean active,
            final boolean createSynced,
            final Boolean closeSynced
    ) {
        this.id = id;
        this.sessionId = sessionId;
        this.activityType = activityType;
        this.caloriesCalculationMethod = caloriesCalculationMethod;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.active = active;
        this.createSynced = createSynced;
        this.closeSynced = closeSynced;
    }

    @Ignore
    public Session(
            final String sessionId,
            final Session.ActivityType activityType,
            final Calories.CalculationMethod caloriesCalculationMethod,
            final Date createdAt,
            final Date closedAt,
            final boolean active,
            final boolean createSynced,
            final Boolean closeSynced
    ) {
        this(
                0,
                sessionId,
                activityType,
                caloriesCalculationMethod,
                createdAt,
                closedAt,
                active,
                createSynced, closeSynced
        );
    }

    @Override
    public Session duplicate(final Map<String, Object> parameters) {
        final long id = parameters.containsKey("id") ? (long) parameters.get("id") : this.id;
        final String sessionId = parameters.containsKey("sessionId") ? (String) parameters.get("sessionId") : this.sessionId;
        final Calories.CalculationMethod caloriesCalculationMethod = parameters.containsKey("caloriesCalculationMethod") ? (Calories.CalculationMethod) parameters.get("caloriesCalculationMethod") : this.caloriesCalculationMethod;
        final Date closedAt = parameters.containsKey("closedAt") ? (Date) parameters.get("closedAt") : this.closedAt;
        final boolean createSynced = parameters.containsKey("createSynced") ? (boolean) parameters.get("createSynced") : this.createSynced;
        final Boolean closeSynced = parameters.containsKey("closeSynced") ? (Boolean) parameters.get("closeSynced") : this.closeSynced;

        return new Session(
                id,
                sessionId,
                this.activityType,
                caloriesCalculationMethod,
                this.createdAt,
                closedAt,
                this.active,
                createSynced,
                closeSynced
        );
    }
}
