package org.ranapat.sensors.gps.example.data.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = "calories",
        indices = @Index(value = {"package_id"}),
        foreignKeys = @ForeignKey(
                entity = Package.class,
                parentColumns = "id",
                childColumns = "package_id",
                onDelete = CASCADE
        ))
public final class Calories implements DataEntity, Duplicatable<Calories> {
    public enum CalculationMethod {
        Undefined,
        Basic_v1
    }

    public static Calories undefined() {
        return new Calories(
                -1, null, 0.0f, null
        );
    }

    @PrimaryKey(autoGenerate = true)
    public final long id;

    @ColumnInfo(name = "package_id")
    public final Long packageId;

    @ColumnInfo(name = "accumulated_locally")
    public final float accumulatedLocally;

    @ColumnInfo(name = "accumulated_remotely")
    public final Float accumulatedRemotely;

    public Calories(
            final long id,
            final Long packageId,
            final float accumulatedLocally,
            final Float accumulatedRemotely
    ) {
        this.id = id;
        this.packageId = packageId;
        this.accumulatedLocally = accumulatedLocally;
        this.accumulatedRemotely = accumulatedRemotely;
    }

    @Ignore
    public Calories(
            final float accumulatedLocally,
            final Float accumulatedRemotely
    ) {
        this(
                0, null,
                accumulatedLocally, accumulatedRemotely
        );
    }

    @Override
    public Calories duplicate(final Map<String, Object> parameters) {
        final long id = parameters.containsKey("id") ? (long) parameters.get("id") : this.id;
        final Long packageId = parameters.containsKey("packageId") ? (Long) parameters.get("packageId") : this.packageId;
        final Float accumulatedRemotely = parameters.containsKey("accumulatedRemotely") ? (Float) parameters.get("accumulatedRemotely") : this.accumulatedRemotely;

        return new Calories(
                id,
                packageId,
                this.accumulatedLocally,
                accumulatedRemotely
        );
    }
}
