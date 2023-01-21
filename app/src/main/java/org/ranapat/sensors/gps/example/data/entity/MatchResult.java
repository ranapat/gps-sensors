package org.ranapat.sensors.gps.example.data.entity;

public class MatchResult implements DataEntity {
    public static class MatchPlayer implements DataEntity {
        public final long userId;
        public final double oldRating;
        public final double newRating;

        public MatchPlayer(
                final long userId,
                final double oldRating,
                final double newRating
        ) {
            this.userId = userId;
            this.oldRating = oldRating;
            this.newRating = newRating;
        }
    }

    public final MatchPlayer winner;
    public final MatchPlayer loser;

    public MatchResult(final MatchPlayer winner, final MatchPlayer loser) {
        this.winner = winner;
        this.loser = loser;
    }
}
