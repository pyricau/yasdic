/**
 * 
 */
package info.piwai.yasdic;

import java.util.List;

/**
 * @author Pierre-Yves Ricau
 * 
 */
public class CyclicDependencyRuntimeException extends YasdicRuntimeException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public CyclicDependencyRuntimeException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(List<String> dependencyStack) {
		super(dependencyStack);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(String detailMessage, List<String> dependencyStack) {
		super(detailMessage, dependencyStack);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(throwable, dependencyStack);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 * @param dependencyStack
	 */
	public CyclicDependencyRuntimeException(String detailMessage, YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(detailMessage, throwable, dependencyStack);
		// TODO Auto-generated constructor stub
	}

}
