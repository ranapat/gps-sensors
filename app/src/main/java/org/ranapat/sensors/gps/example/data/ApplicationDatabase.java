package org.ranapat.sensors.gps.example.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.ranapat.instancefactory.Static;
import org.ranapat.sensors.gps.example.SensorsApplication;
import org.ranapat.sensors.gps.example.data.dao.CaloriesDao;
import org.ranapat.sensors.gps.example.data.dao.DefaultHeartRateDao;
import org.ranapat.sensors.gps.example.data.dao.DefaultSpeedMetersPerSecondDao;
import org.ranapat.sensors.gps.example.data.dao.LocationDao;
import org.ranapat.sensors.gps.example.data.dao.PackageDao;
import org.ranapat.sensors.gps.example.data.dao.SessionDao;
import org.ranapat.sensors.gps.example.data.dao.StepSummaryDao;
import org.ranapat.sensors.gps.example.data.dao.UserDao;
import org.ranapat.sensors.gps.example.data.entity.Calories;
import org.ranapat.sensors.gps.example.data.entity.DefaultHeartRate;
import org.ranapat.sensors.gps.example.data.entity.DefaultSpeedMetersPerSecond;
import org.ranapat.sensors.gps.example.data.entity.Location;
import org.ranapat.sensors.gps.example.data.entity.Package;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.StepSummary;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.data.tools.Converters;

@Database(
        entities = {
                DefaultHeartRate.class,
                DefaultSpeedMetersPerSecond.class,

                User.class,

                Session.class,
                Package.class,
                Location.class,
                StepSummary.class,
                Calories.class
        },
        version = 1,
        exportSchema = true
)
@TypeConverters({
        Converters.class
})
@Static
public abstract class ApplicationDatabase extends RoomDatabase {
    private static final String DB_NAME = "application.db";
    private static volatile ApplicationDatabase instance;

    public static synchronized ApplicationDatabase getInstance() {
        if (instance == null) {
            instance = create(SensorsApplication.getAppContext());
        }
        return instance;
    }

    @NonNull
    private static ApplicationDatabase create(final Context context) {
        return Room.databaseBuilder(
                context, ApplicationDatabase.class, DB_NAME)
                .addMigrations(
                        //
                )
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract DefaultHeartRateDao defaultHeartRateDao();
    public abstract DefaultSpeedMetersPerSecondDao defaultSpeedMetersPerSecondDao();

    public abstract UserDao userDao();

    public abstract SessionDao sessionDao();
    public abstract PackageDao packageDao();
    public abstract LocationDao locationDao();
    public abstract StepSummaryDao stepSummaryDao();
    public abstract CaloriesDao caloriesDao();
}
