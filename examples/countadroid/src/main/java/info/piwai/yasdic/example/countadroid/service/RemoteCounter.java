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

package info.piwai.yasdic.example.countadroid.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

/**
 * 
 * @author Pierre-Yves Ricau (py.ricau+countadroid@gmail.com)
 * 
 */
public class RemoteCounter implements ICounter {

	private static final String	TAG	= RemoteCounter.class.getSimpleName();

	private HttpClient			httpClient;
	private String				counterURL;

	public int increment() {
		return getCountFromUrl(counterURL + "?increment");
	}

	public int getCount() {
		return getCountFromUrl(counterURL);
	}

	private int getCountFromUrl(String url) {
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpClient.execute(request);

			int status = response.getStatusLine().getStatusCode();

			if (status != HttpStatus.SC_OK) {
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				response.getEntity().writeTo(ostream);
				Log.e(TAG, ostream.toString());
			} else {
				InputStream content = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content), 10);
				String count = reader.readLine();

				content.close();

				return Integer.parseInt(count);
			}

		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		return 0;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String getCounterURL() {
		return counterURL;
	}

	public void setCounterURL(String counterURL) {
		this.counterURL = counterURL;
	}

}
