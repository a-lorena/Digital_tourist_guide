package com.example.digitaltouristguide.Models;

import com.google.firebase.Timestamp;

public class CommentModel {
	String author, text, id;
	Timestamp timestamp;

	public CommentModel() {
	}

	public CommentModel(String author, String text, String id, Timestamp timestamp) {
		this.author = author;
		this.text = text;
		this.id = id;
		this.timestamp = timestamp;
	}

	public String getAuthor() {
		return author;
	}

	public String getText() {
		return text;
	}

	public String getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
}
