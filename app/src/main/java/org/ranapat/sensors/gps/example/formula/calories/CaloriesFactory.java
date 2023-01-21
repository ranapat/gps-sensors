package org.ranapat.sensors.gps.example.formula.calories;

import org.ranapat.sensors.gps.example.data.entity.Calories;
import org.ranapat.sensors.gps.example.formula.Formula;

public final class CaloriesFactory {
    private CaloriesFactory() {
        //
    }

    public static Formula<Float> get(final Calories.CalculationMethod calculationMethod) {
        if (calculationMethod == Calories.CalculationMethod.Basic_v1) {
            return new BasicV1();
        } else if (calculationMethod == Calories.CalculationMethod.Undefined) {
            return new BasicV1();
        } else {
            return null;
        }
    }
}
