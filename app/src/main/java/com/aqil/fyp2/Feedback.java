package com.aqil.fyp2;

public class Feedback {
    public String userId;
    public float rating;
    public String comment;

    public Feedback() {
        // Default constructor required for Firebase
    }

    public Feedback(String userId, float rating, String comment) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }
}

