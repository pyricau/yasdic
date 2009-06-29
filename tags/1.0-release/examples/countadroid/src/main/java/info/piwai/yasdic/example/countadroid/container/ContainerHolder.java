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

package info.piwai.yasdic.example.countadroid.container;

import info.piwai.yasdic.YasdicContainer;
import info.piwai.yasdic.YasdicContainer.BeanDef;
import info.piwai.yasdic.example.countadroid.service.LocalCounter;
import info.piwai.yasdic.example.countadroid.service.RemoteCounter;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * This class is a holder on the YasdicContainer, designed with the singleton
 * pattern.
 * 
 * @author Pierre-Yves Ricau (py.ricau+countadroid@gmail.com)
 * 
 */
public class ContainerHolder {

	// Singleton holder
	private static YasdicContainer	container;

	/**
	 * Returns a singleton instance of the container. The first call on this
	 * method creates the singleton container (and insert the bean definitions
	 * into it). The next calls return the same instance of the container.
	 * 
	 * @return
	 */
	public static YasdicContainer getInstance() {
		if (container == null) {
			container = new YasdicContainer();
			defineBeans(container);
		}
		return container;
	}

	/**
	 * This method is used to define the beans commons to all activities (yeah,
	 * sure, there is only one activity in this application. But you usually
	 * have more then once ;-D )
	 * 
	 * @param container
	 *            The Yasdic Container
	 */
	private static void defineBeans(YasdicContainer container) {

		/*
		 * Defining the counter url for the remoteCounter. You may change this
		 * value if your remote counting script is not on the same path.
		 * 
		 * Please note that 10.0.2.2 is a virtual IP to represent the localhost
		 * on your computer.
		 */
		container.define("counterUrl", "http://10.0.2.2/countadroid/count.php");

		container.define("localCounter", new LogBeanDef<LocalCounter>() {
			@Override
			protected LocalCounter newBean(YasdicContainer c) {
				return new LocalCounter();
			}
		});

		container.define("remoteCounter", new LogBeanDef<RemoteCounter>() {
			@Override
			protected RemoteCounter newBean(YasdicContainer c) {
				return new RemoteCounter();
			}

			@Override
			protected void initBean(YasdicContainer c, RemoteCounter bean) {
				bean.setCounterURL((String) c.getBean("counterUrl"));
				bean.setHttpClient((HttpClient) c.getBean("counterHttpClient"));
			}
		});

		container.define("counterHttpClient", new LogBeanDef<HttpClient>() {
			@Override
			protected HttpClient newBean(YasdicContainer c) {
				return new DefaultHttpClient();
			}
		});

	}

	/**
	 * This class extends BeanDef to add logging ability with the Android
	 * Logger. You should use Logcat when running the Android application, to
	 * see when instances are created. You should notice that instances are
	 * created only when they are needed.
	 * 
	 * @author Pierre-Yves Ricau (py.ricau+countadroid@gmail.com)
	 * 
	 * @param <T>
	 */
	public static abstract class LogBeanDef<T> extends BeanDef<T> {
		private static final String	TAG	= LogBeanDef.class.getSimpleName();

		@Override
		protected final T callNewBean(YasdicContainer c, String id) {
			// Calling the newBean method (to actually instanciate the bean)
			T bean = newBean(c);

			// Building the log String
			StringBuilder sb = new StringBuilder("******** Bean [");
			sb.append(id).append("] created, instance of [").append(
					bean.getClass().getSimpleName()).append("]");

			Log.d(TAG, sb.toString());
			return bean;
		}
	}

}
