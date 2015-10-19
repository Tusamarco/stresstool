package net.tc.stresstool.exceptions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StressToolException extends Exception {

    public StressToolException() {
	super();
    }

    public StressToolException(String message) {
	super(message);
    }

    public StressToolException(Throwable cause) {
	super(cause);
    }

    public StressToolException(String message, Throwable cause) {
	super(message, cause);
    }

    @Override
    public void printStackTrace() {
	super.printStackTrace();
	if (ExceptionMessages.getCurrentError() > ExceptionMessages.ERROR_RECOVERABLE)
	    System.exit(1);
    }

    public StressToolException(Exception e) {
	StringBuffer sb = new StringBuffer();
	ByteArrayOutputStream error = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(error);
	e.printStackTrace(ps);

	new StressToolConfigurationException(error.toString());
	// TODO Auto-generated constructor stub

    }
}