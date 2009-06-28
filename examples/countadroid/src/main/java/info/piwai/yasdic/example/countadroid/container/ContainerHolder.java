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
 * 
 * @author Pierre-Yves Ricau (py.ricau+countadroid@gmail.com)
 * 
 */
public class ContainerHolder {

	private static YasdicContainer	container;

	public static YasdicContainer getInstance() {
		if (container == null) {
			container = new YasdicContainer();
			defineBeans(container);
		}
		return container;
	}

	private static void defineBeans(YasdicContainer container) {

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

	public static abstract class LogBeanDef<T> extends BeanDef<T> {
		private static final String	TAG	= LogBeanDef.class.getSimpleName();

		@Override
		protected final T callNewBean(YasdicContainer c, String id) {
			T bean = newBean(c);
			StringBuilder sb = new StringBuilder("******** Bean [");
			sb.append(id).append("] created, instance of [").append(
					bean.getClass().getSimpleName()).append("]");
			Log.d(TAG, sb.toString());
			return bean;
		}
	}

}
