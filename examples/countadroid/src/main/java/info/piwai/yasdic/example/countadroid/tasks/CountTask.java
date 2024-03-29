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

package info.piwai.yasdic.example.countadroid.tasks;

import info.piwai.yasdic.example.countadroid.activity.ICountingActivity;
import info.piwai.yasdic.example.countadroid.service.ICounter;
import android.os.AsyncTask;

/**
 * The CountTask does not know the counter implementation used.
 * 
 * Should you wonder what is an AsyncTask, please feel free to read this:
 * http://developer.android.com/reference/android/os/AsyncTask.html
 * 
 * @author Pierre-Yves Ricau (py.ricau+countadroid@gmail.com)
 * 
 */
public class CountTask extends AsyncTask<Object, Object, Integer> {

	private ICounter			counter;
	private ICountingActivity	activity;

	@Override
	protected Integer doInBackground(Object... params) {
		return counter.getCount();
	}

	@Override
	protected void onPostExecute(Integer result) {
		activity.onCountComplete(result);
	}

	public ICounter getCounter() {
		return counter;
	}

	public void setCounter(ICounter counter) {
		this.counter = counter;
	}

	public ICountingActivity getActivity() {
		return activity;
	}

	public void setActivity(ICountingActivity activity) {
		this.activity = activity;
	}

}
