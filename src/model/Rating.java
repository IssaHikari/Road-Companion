package model;

import java.sql.Timestamp;

public class Rating {
    private int ratingId;
    private int ratedUserId; // Driver
    private int raterUserId; // Passenger
    private int score; // 1-5
    private String comment;
    private Timestamp createdAt;

    public Rating() {
    }

    public Rating(int ratedUserId, int raterUserId, int score, String comment) {
        this.ratedUserId = ratedUserId;
        this.raterUserId = raterUserId;
        this.score = score;
        this.comment = comment;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(int ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public int getRaterUserId() {
        return raterUserId;
    }

    public void setRaterUserId(int raterUserId) {
        this.raterUserId = raterUserId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
