package net.tc.stresstool.exceptions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

public class StressToolSQLException extends SQLException {
    @Override
    public void printStackTrace() {
	super.printStackTrace();
	if (ExceptionMessages.getCurrentError() > ExceptionMessages.ERROR_RECOVERABLE)
	    System.exit(1);
    }

    public StressToolSQLException(Exception e) {
	StringBuffer sb = new StringBuffer();
	ByteArrayOutputStream error = new ByteArrayOutputStream();
	PrintStream ps = new PrintStream(error);
	e.printStackTrace(ps);

	new StressToolConfigurationException(error.toString());
	// TODO Auto-generated constructor stub

    }
	public StressToolSQLException() {
		// TODO Auto-generated constructor stub
	}

	public StressToolSQLException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StressToolSQLException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public StressToolSQLException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}


}
