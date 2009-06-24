package info.piwai.yasdic.example.smallmvc.impl;

import info.piwai.yasdic.example.smallmvc.interfaces.IView;

import java.io.PrintStream;

public class ViewImpl implements IView {

	private String		appName;
	private PrintStream	printStream;

	public ViewImpl(String appName, PrintStream printStream) {
		this.appName = appName;
		this.printStream = printStream;
	}

	public void renderView(String param) {
		printStream.println("Do you know how " + param + " " + appName
				+ " is ?");
	}

}
