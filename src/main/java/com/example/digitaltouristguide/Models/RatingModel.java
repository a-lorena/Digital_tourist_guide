package com.example.digitaltouristguide.Models;

import com.google.firebase.Timestamp;

public class RatingModel {
	String author, comment;
	int stars;
	Timestamp time;

	public RatingModel() {
	}

	public RatingModel(String author, String comment, int stars, Timestamp time) {
		this.author = author;
		this.comment = comment;
		this.stars = stars;
		this.time = time;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}
}
