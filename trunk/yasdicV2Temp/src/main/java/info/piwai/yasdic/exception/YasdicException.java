/**
 * 
 */
package info.piwai.yasdic.exception;

/**
 * @author Pierre-Yves Ricau
 * 
 */
public class YasdicException extends Exception {

	private static final long	serialVersionUID	= 1L;

	/**
	 * 
	 */
	public YasdicException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public YasdicException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public YasdicException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public YasdicException(String message, Throwable cause) {
		super(message, cause);
	}

}
