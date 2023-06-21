package com.example.digitaltouristguide.Models;

public class NatureModel {
	private String name, type, town, desc, imageName;
	private Long lat, lon, comments, stars, rating;

	public NatureModel() {
	}

	public NatureModel(String name, String type, String town, String desc, String imageName,
					   Long lat, Long lon, Long comments, Long stars, Long rating) {
		this.name = name;
		this.type = type;
		this.town = town;
		this.desc = desc;
		this.imageName = imageName;
		this.lat = lat;
		this.lon = lon;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Long getLat() {
		return lat;
	}

	public void setLat(Long lat) {
		this.lat = lat;
	}

	public Long getLon() {
		return lon;
	}

	public void setLon(Long lon) {
		this.lon = lon;
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
