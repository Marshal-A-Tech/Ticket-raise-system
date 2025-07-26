package com.itil.dto;

import jakarta.validation.constraints.NotEmpty;

public class TrainingDTO {
    @NotEmpty(message = "Training name should not be empty")
    private String trainingName;
    @NotEmpty(message = "Description should not be empty")
    private String description;
    @NotEmpty(message = "Trainer name should not be empty")
    private String trainer;

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }
}
