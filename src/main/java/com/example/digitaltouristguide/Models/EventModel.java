package com.example.digitaltouristguide.Models;

public class EventModel {
	private String name, town, location, dateStart, dateEnd, start, end, description, imageName;
	private float comments, stars, rating;

	public EventModel() {
	}

	public EventModel(String name, String town, String location, String dateStart, String dateEnd,
					  String start, String end, String description, String imageName,
					  float comments, float stars, float rating) {
		this.name = name;
		this.town = town;
		this.location = location;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.start = start;
		this.end = end;
		this.description = description;
		this.imageName = imageName;
		this.comments = comments;
		this.stars = stars;
		this.rating = rating;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public float getComments() {
		return comments;
	}

	public void setComments(float comments) {
		this.comments = comments;
	}

	public float getStars() {
		return stars;
	}

	public void setStars(float stars) {
		this.stars = stars;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}
}
