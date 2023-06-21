package com.example.digitaltouristguide.Models;

public class InstitutionModel {
	private String name, type, town, workingHours, description, imageName;
	private float latitude, longitude, comments, stars, rating;

	public InstitutionModel() {
	}

	public InstitutionModel(String name, String type, String town, String workingHours,
							String description, String imageName, float latitude,
							float longitude, float comments, float stars, float rating) {
		this.name = name;
		this.type = type;
		this.town = town;
		this.workingHours = workingHours;
		this.description = description;
		this.imageName = imageName;
		this.latitude = latitude;
		this.longitude = longitude;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
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

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
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
