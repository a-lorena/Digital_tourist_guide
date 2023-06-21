package com.example.digitaltouristguide.Models;

public class TownModel {
	private String name, county, description, imageName;
	private Long latitude, longitude, comments, stars, rating;

	public TownModel() {
	}

	public TownModel(String name, String county, String description, String imageName,
					 Long latitude, Long longitude, Long comments, Long stars, Long rating) {
		this.name = name;
		this.county = county;
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

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
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

	public Long getLatitude() {
		return latitude;
	}

	public void setLatitude(Long latitude) {
		this.latitude = latitude;
	}

	public Long getLongitude() {
		return longitude;
	}

	public void setLongitude(Long longitude) {
		this.longitude = longitude;
	}

	public Long getComments() {
		return comments;
	}

	public void setComments(Long comments) {
		this.comments = comments;
	}

	public Long getStars() {
		return stars;
	}

	public void setStars(Long stars) {
		this.stars = stars;
	}

	public Long getRating() {
		return rating;
	}

	public void setRating(Long rating) {
		this.rating = rating;
	}
}
