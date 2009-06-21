package info.piwai.yasdic.example.impl;

import info.piwai.yasdic.example.interfaces.IStorage;

public class User {

	IStorage	storage;

	public User(IStorage storage) {
		this.storage = storage;
	}

}
