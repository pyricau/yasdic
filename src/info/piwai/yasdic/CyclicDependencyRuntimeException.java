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

package info.piwai.yasdic;

import java.util.List;

/**
 * 
 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
 * 
 */
public class CyclicDependencyRuntimeException extends YasdicRuntimeException {

	private static final long	serialVersionUID	= 1L;

	public CyclicDependencyRuntimeException() {
	}

	/**
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(List<String> dependencyStack) {
		super(dependencyStack);
	}

	/**
	 * @param detailMessage
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(String detailMessage, List<String> dependencyStack) {
		super(detailMessage, dependencyStack);
	}

	/**
	 * @param throwable
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(throwable, dependencyStack);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(String detailMessage, YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(detailMessage, throwable, dependencyStack);
	}

}
