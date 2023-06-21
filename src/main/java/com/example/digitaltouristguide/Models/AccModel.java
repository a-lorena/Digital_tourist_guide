package com.example.digitaltouristguide.Models;

public class AccModel {
	String name, type, town, price, description, imageName;
	Long latitude, longitude, comments, stars, rating;

	public AccModel() {
	}

	public AccModel(String name, String type, String town, String description, String imageName,
					Long latitude, Long longitude, String price, Long comments, Long stars, Long rating) {
		this.name = name;
		this.type = type;
		this.town = town;
		this.description = description;
		this.imageName = imageName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.price = price;
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
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
