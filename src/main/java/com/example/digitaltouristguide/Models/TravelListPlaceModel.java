package com.example.digitaltouristguide.Models;

public class TravelListPlaceModel {
	String placeID, listID, name, category;
	Boolean visited;

	public TravelListPlaceModel() {
	}

	public TravelListPlaceModel(String placeID, String listID, String name, String category, Boolean visited) {
		this.placeID = placeID;
		this.listID = listID;
		this.name = name;
		this.category = category;
		this.visited = visited;
	}

	public String getPlaceID() {
		return placeID;
	}

	public void setPlaceID(String placeID) {
		this.placeID = placeID;
	}

	public String getListID() {
		return listID;
	}

	public void setListID(String listID) {
		this.listID = listID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public Boolean getVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}
}
