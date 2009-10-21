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

package info.piwai.yasdic.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
 * 
 */
public class YasdicRuntimeException extends RuntimeException {

	private ArrayList<String>	dependencyStack;

	private static final long	serialVersionUID	= 1L;

	private static final String	STACK_INTRO			= " Dependency Stack: ";

	public static String formatDependencyStack(List<String> dependencyStack) {
		StringBuilder sb = new StringBuilder("[");
		boolean first = true;
		for (String dependency : dependencyStack) {
			if (first) {
				first = false;
			} else {
				sb.append(">");
			}
			sb.append(dependency);
		}
		sb.append("]");

		return sb.toString();
	}

	public static String formatDependencyStack(List<String> initialDependencyStack, List<String> appendedDependencyStack) {
		return formatDependencyStack(initialDependencyStack) + " > " + formatDependencyStack(appendedDependencyStack);
	}

	public YasdicRuntimeException() {
	}

	public YasdicRuntimeException(List<String> dependencyStack) {
		this.dependencyStack = new ArrayList<String>(dependencyStack);
	}

	public YasdicRuntimeException(String detailMessage, List<String> dependencyStack) {
		super(detailMessage + STACK_INTRO + formatDependencyStack(dependencyStack));
		this.dependencyStack = new ArrayList<String>(dependencyStack);
	}

	public YasdicRuntimeException(RuntimeException cause, String detailMessage, List<String> dependencyStack) {
		super(detailMessage + STACK_INTRO + formatDependencyStack(dependencyStack), cause);
		this.dependencyStack = new ArrayList<String>(dependencyStack);
	}

	/**
	 * @param throwable
	 */
	public YasdicRuntimeException(YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(STACK_INTRO + formatDependencyStack(dependencyStack, throwable.dependencyStack), throwable);
		this.dependencyStack = new ArrayList<String>(dependencyStack);
		dependencyStack.addAll(throwable.dependencyStack);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public YasdicRuntimeException(String detailMessage, YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(detailMessage + STACK_INTRO + formatDependencyStack(dependencyStack, throwable.dependencyStack), throwable);
		this.dependencyStack = new ArrayList<String>(dependencyStack);
		dependencyStack.addAll(throwable.dependencyStack);
	}

	public List<String> getDependencyStack() {
		return dependencyStack;
	}

}
