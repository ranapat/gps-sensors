package org.ranapat.sensors.gps.example.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = "users",
        indices = @Index(value = {"username"}, unique = true))
public class User implements DataEntity, Duplicatable<User> {
    public enum Gender {
        Male,
        Female,
        Other
    }

    @PrimaryKey(autoGenerate = true)
    public final long id;

    @ColumnInfo(name = "username")
    public final String username;

    @ColumnInfo(name = "age")
    public final int age;

    @ColumnInfo(name = "gender")
    public final User.Gender gender;

    @ColumnInfo(name = "free_run_session_id")
    public final String freeRunSessionId;

    @ColumnInfo(name = "weight")
    public final float weight;

    @ColumnInfo(name = "rating")
    public final double rating;

    public User(
            final long id,
            final String username,
            final int age,
            final User.Gender gender,
            final String freeRunSessionId,
            final float weight,
            final double rating
    ) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.freeRunSessionId = freeRunSessionId;
        this.weight = weight;
        this.rating = rating;
    }

    @Override
    public User duplicate(final Map<String, Object> parameters) {
        final long id = parameters.containsKey("id") ? (long) parameters.get("id") : this.id;
        final int age = parameters.containsKey("age") ? (int) parameters.get("age") : this.age;
        final User.Gender gender = parameters.containsKey("gender") ? (User.Gender) parameters.get("gender") : this.gender;
        final float weight = parameters.containsKey("weight") ? (float) parameters.get("weight") : this.weight;
        final double rating = parameters.containsKey("rating") ? (double) parameters.get("rating") : this.rating;

        return new User(
                id,
                this.username,
                age,
                gender,
                this.freeRunSessionId,
                weight,
                rating
        );
    }

}
