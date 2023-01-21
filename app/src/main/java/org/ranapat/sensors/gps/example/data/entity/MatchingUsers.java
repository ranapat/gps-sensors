package org.ranapat.sensors.gps.example.data.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchingUsers implements DataEntity {
    private final static Comparator<User> comparatorAbove = new Comparator<User>() {
        @Override
        public int compare(final User o1, final User o2) {
            return Double.compare(o1.rating, o2.rating);
        }
    };
    private final static Comparator<User> comparatorBelow = new Comparator<User>() {
        @Override
        public int compare(final User o1, final User o2) {
            final int code = Double.compare(o1.rating, o2.rating);
            return code == 1 ? -1 : code == -1 ? 1 : 0;
        }
    };

    public final User current;
    public final List<User> below;
    public final List<User> above;

    public MatchingUsers(
        final User current,
        final List<User> below,
        final List<User> above
    ) {
        this.current = current;
        this.below = below;
        this.above = above;

        Collections.sort(this.below, comparatorBelow);
        Collections.sort(this.above, comparatorAbove);
    }

}
