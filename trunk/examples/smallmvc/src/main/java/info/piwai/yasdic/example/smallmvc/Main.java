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
package info.piwai.yasdic.example.smallmvc;

import info.piwai.yasdic.YasdicContainer;
import info.piwai.yasdic.YasdicContainer.BeanDef;
import info.piwai.yasdic.example.smallmvc.impl.ControllerImpl;
import info.piwai.yasdic.example.smallmvc.impl.ServiceImpl;
import info.piwai.yasdic.example.smallmvc.impl.ViewImpl;
import info.piwai.yasdic.example.smallmvc.interfaces.IController;
import info.piwai.yasdic.example.smallmvc.interfaces.IService;
import info.piwai.yasdic.example.smallmvc.interfaces.IView;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Creating a new container
		YasdicContainer container = new YasdicContainer();

		// Defining the needed beans
		defineBeans(container);

		/*
		 * Getting a IController bean of scope prototype that possess a IService
		 * bean a scope prototype that will use the bean niceWords
		 */
		IController controller1 = (IController) container
				.getBean("myController");

		/*
		 * Overriding the bean niceWords (we could also use a BeanDef instead,
		 * but this is to show how to inject singletons in the container,
		 * instead of defining them)
		 */
		List<String> words = new ArrayList<String>();
		words.add("NIIICE");
		words.add("COOOL");
		container.define("niceWords", words);

		IController controller2 = (IController) container
				.getBean("myController");

		// Showing results :
		System.out.println("First Controller:");
		for (int i = 0; i < 5; i++) {
			controller1.execute();
		}
		System.out.println("\nSecond Controller:");
		for (int i = 0; i < 5; i++) {
			controller2.execute();
		}

	}

	public static void defineBeans(YasdicContainer container) {

		/*
		 * Defining a new bean myController of type Controller Impl with scope
		 * prototype. It has a IService dependency with id myService and a IView
		 * dependency with id myView
		 */
		container.define("myController", false, new BeanDef<IController>() {
			@Override
			public IController newBean(YasdicContainer c) {
				return new ControllerImpl((IService) c.getBean("myService"),
						(IView) c.getBean("myView"));
			}
		});

		/*
		 * Defining a new bean myService of type ServiceImpl with scope
		 * prototype. It has a List dependency with id niceWords
		 */
		container.define("myService", false, new BeanDef<IService>() {
			@SuppressWarnings("unchecked")
			@Override
			public IService newBean(YasdicContainer c) {
				return new ServiceImpl((List<String>) c.getBean("niceWords"));
			}
		});

		/*
		 * Defining a new bean myView of type ViewImpl with scope singleton. It
		 * has no dependencies
		 */
		container.define("myView", new BeanDef<IView>() {
			@Override
			public IView newBean(YasdicContainer c) {
				return new ViewImpl((String) c.getBean("appName"),
						(PrintStream) c.getBean("printStream"));
			}
		});

		/*
		 * Defining a new bean niceWords of type ArrayList with scope singleton.
		 * It has no dependencies
		 */
		container.define("niceWords", new BeanDef<List<String>>() {
			@Override
			public List<String> newBean(YasdicContainer c) {
				List<String> words = new ArrayList<String>();
				words.add("FUUUNKY");
				words.add("GREEEAAT");
				words.add("SEEEXYY");
				return words;
			}
		});

		// Injecting a bean appName of type String
		container.define("appName", "Android");

		// Injecting System.out as the printStream
		container.define("printStream", System.out);

	}

}
