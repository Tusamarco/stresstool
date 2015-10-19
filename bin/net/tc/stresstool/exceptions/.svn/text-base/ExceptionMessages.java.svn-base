package net.tc.stresstool.exceptions;

public class ExceptionMessages {
	public static int errorLevels[] = new int[]{100,200,300,400}; //100 info, 200 warning, 300 error,400 fatal
	public static int ERROR_INFO = 100;
	public static int ERROR_WARNING = 200;
	public static int ERROR_RECOVERABLE = 300;
	public static int ERROR_FATAL = 400;
	private static int currentError =0;
	
	public static String INVALID_CONFIGURATION_REQUEST = "\nThe Current configuration request is not correct item not found ";
	public static String INVALID_LOG_REQUEST = "\nThe Current LOG request is not correct: ";
	public static String INVALID_PERMISSIONS = "\nNot enough previliges check application log for details.";
	public static String ERROR_PORCESSING_STATS = "\nThere is a problem processing the statistics see details:";
	public static String ERROR_CREATING_PROVIDER = "There is a problem creating the provider check below for more details:";
	public static String ERROR_METHOD_IN_SUPERCLASS = "Invocation Error, this method should be implemented in a extending class with @Override";
	
	public ExceptionMessages() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the currentError
	 */
	public static final int getCurrentError() {
		return currentError;
	}

	/**
	 * @param currentError the currentError to set
	 */
	public static final void setCurrentError(int currentError) {
		ExceptionMessages.currentError = currentError;
	}
	

}
