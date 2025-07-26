package com.itil.dto;

import com.itil.entity.User;

import java.util.List;

public class TrainingResponseDTO {

    private String trainingName;
    private String trainer;
    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

}
