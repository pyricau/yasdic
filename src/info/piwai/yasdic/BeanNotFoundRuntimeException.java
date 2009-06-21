/**
 * 
 */
package info.piwai.yasdic;

import java.util.List;

/**
 * @author Pierre-Yves Ricau
 * 
 */
public class BeanNotFoundRuntimeException extends YasdicRuntimeException {

	/**
	 * 
	 */
	public BeanNotFoundRuntimeException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dependencyStack
	 */
	public BeanNotFoundRuntimeException(List<String> dependencyStack) {
		super(dependencyStack);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 * @param dependencyStack
	 */
	public BeanNotFoundRuntimeException(String detailMessage, List<String> dependencyStack) {
		super(detailMessage, dependencyStack);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 * @param dependencyStack
	 */
	public BeanNotFoundRuntimeException(YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(throwable, dependencyStack);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 * @param dependencyStack
	 */
	public BeanNotFoundRuntimeException(String detailMessage, YasdicRuntimeException throwable, List<String> dependencyStack) {
		super(detailMessage, throwable, dependencyStack);
		// TODO Auto-generated constructor stub
	}

}
