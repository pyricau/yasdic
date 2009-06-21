package info.piwai.yasdic.example.impl;

import info.piwai.yasdic.example.interfaces.IStorage;

public class SessionStorage implements IStorage {

	private String	cookieName;

	public SessionStorage() {

	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

}
