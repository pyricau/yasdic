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

/**
 * A super simple counter.
 * 
 * Note that its state has an instance scope, which means that the count is lost
 * when creating a new instance or restarting the android application.
 * 
 * @author Pierre-Yves Ricau (py.ricau+countadroid@gmail.com)
 * 
 */
public class LocalCounter implements ICounter {

	private int	count	= 0;

	public int increment() {
		count++;
		return count;
	}

	public int getCount() {
		return count;
	}

}
