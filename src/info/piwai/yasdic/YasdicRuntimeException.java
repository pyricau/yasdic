/**
 * 
 */
package info.piwai.yasdic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierre-Yves Ricau
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
