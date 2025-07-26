package com.itil.entity;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="training")
public class Training {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String trainingName;
	private String description;
	private String trainer;
	@OneToMany(mappedBy = "training", cascade = CascadeType.ALL)
	private List<Ticket> tickets;
	private List<String> feedbacks;
	private ArrayList<Double> rating;

	public Training() {

	}

	public Training(Long id, String trainingName, String description, String trainer, List<Ticket> tickets, List<String> feedbacks, ArrayList<Double> rating) {
		this.id = id;
		this.trainingName = trainingName;
		this.description = description;
		this.trainer = trainer;
		this.tickets = tickets;
		this.feedbacks = feedbacks;
		this.rating = rating;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public List<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}

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
