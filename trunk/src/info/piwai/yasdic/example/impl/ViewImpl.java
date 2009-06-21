package info.piwai.yasdic.example.impl;

import info.piwai.yasdic.example.interfaces.IView;

public class ViewImpl implements IView {

	private String	appName;

	public ViewImpl(String appName) {
		this.appName = appName;
	}

	public String renderView(String param) {
		return "Do you know how " + param + " " + appName + " is ?";
	}

}
