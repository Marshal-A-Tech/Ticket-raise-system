package com.itil.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itil.enums.Role;
import jakarta.persistence.*;


@Entity
@Table(name = "user")
@DiscriminatorValue("USER")
public class User extends UserInfo{
	@OneToMany(mappedBy = "raisedBy", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Ticket> raisedTickets;
	private String certificateName;

	private LocalDate certificateIssueDate;

	public User() {

	}

	public User(Long id, String name, String email, String password, Role role, List<Ticket> raisedTickets, String certificateName, String certificateIssuer, LocalDate certificateIssueDate) {
		super(id, name, email, password, role);
		this.raisedTickets = raisedTickets;
		this.certificateName = certificateName;
		this.certificateIssueDate = certificateIssueDate;
	}

	public List<Ticket> getRaisedTickets() {
		return raisedTickets;
	}

	public void setRaisedTickets(List<Ticket> raisedTickets) {
		this.raisedTickets = raisedTickets;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public LocalDate getCertificateIssueDate() {
		return certificateIssueDate;
	}

	public void setCertificateIssueDate(LocalDate certificateIssueDate) {
		this.certificateIssueDate = certificateIssueDate;
	}
}
