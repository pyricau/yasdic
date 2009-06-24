/*
 * Copyright 2009 Pierre-Yves Ricau
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License. 
 */

package info.piwai.yasdic.example.smallmvc.impl;

import info.piwai.yasdic.example.smallmvc.interfaces.IView;

import java.io.PrintStream;

/**
 * 
 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
 * 
 */
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
