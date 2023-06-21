package com.example.digitaltouristguide.Models;

import com.google.firebase.Timestamp;

public class PutopisModel {
	String title, town, text, author;
	Timestamp timestamp;

	public PutopisModel() {
	}

	public PutopisModel(String title, String town, String text, String author, Timestamp timestamp) {
		this.title = title;
		this.town = town;
		this.text = text;
		this.author = author;
		this.timestamp = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTown() { return town; }

	public void setTown(String town) { this.town = town; }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}
