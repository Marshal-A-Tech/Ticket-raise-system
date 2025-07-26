package com.itil.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TrainingFeedbackDTO {

    private List<String> feedbacks;
    private ArrayList<Double> rating;

    public List<String> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<String> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public ArrayList<Double> getRating() {
        return rating;
    }

    public void setRating(ArrayList<Double> rating) {
        this.rating = rating;
    }
}
