package org.ranapat.sensors.gps.example.data.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = "step_summaries",
        indices = @Index(value = {"package_id"}),
        foreignKeys = @ForeignKey(
                entity = Package.class,
                parentColumns = "id",
                childColumns = "package_id",
                onDelete = CASCADE
        ))
public class StepSummary implements DataEntity, Duplicatable<StepSummary> {
    public static final String METHOD_UNDEFINED = "undefined";

    public static StepSummary undefined() {
        return new StepSummary(
                0, METHOD_UNDEFINED, 0.0, 0.0f
        );
    }

    @PrimaryKey(autoGenerate = true)
    public final long id;

    @ColumnInfo(name = "package_id")
    public final Long packageId;

    @ColumnInfo(name = "steps")
    public final int steps;

    @ColumnInfo(name = "method")
    public final String method;

    @ColumnInfo(name = "accuracy")
    public final double accuracy;

    @ColumnInfo(name = "suspicious")
    public float suspicious;

    public StepSummary(
            final long id,
            final Long packageId,
            final int steps,
            final String method,
            final double accuracy,
            final float suspicious
    ) {
        this.id = id;
        this.packageId = packageId;
        this.steps = steps;
        this.method = method;
        this.accuracy = accuracy;
        this.suspicious = suspicious;
    }

    @Ignore
    public StepSummary(
            final int steps,
            final String method,
            final double accuracy,
            final float suspicious
    ) {
        this(
                0, null,
                steps, method, accuracy,
                suspicious
        );
    }

    @Override
    public StepSummary duplicate(final Map<String, Object> parameters) {
        final long id = parameters.containsKey("id") ? (long) parameters.get("id") : this.id;
        final Long packageId = parameters.containsKey("packageId") ? (Long) parameters.get("packageId") : this.packageId;

        return new StepSummary(
                id,
                packageId,
                this.steps,
                this.method,
                this.accuracy,
                this.suspicious
        );
    }
}
