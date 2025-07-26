package com.itil.dto;

import java.util.ArrayList;

public class FeedbackDTO {
    private String feedback;
    private Double rating;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
