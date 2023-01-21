package org.ranapat.sensors.gps.example.data.entity;

public class RatingEstimation implements DataEntity {
    public final long opponentId;
    public final double pointsToWin;
    public final double pointsToLose;
    public final double quality;

    public RatingEstimation(
            final long opponentId,
            final double pointsToWin,
            final double pointsToLose,
            final double quality
    ) {
        this.opponentId = opponentId;
        this.pointsToWin = pointsToWin;
        this.pointsToLose = pointsToLose;
        this.quality = quality;
    }
}
