package com.example.digitaltouristguide.Models;

import com.google.firebase.Timestamp;

public class NewsModel {
	Timestamp timestamp;
	String text;

	public NewsModel() {
	}

	public NewsModel(Timestamp timestamp, String text) {
		this.timestamp = timestamp;
		this.text = text;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getText() {
		return text;
	}
}
