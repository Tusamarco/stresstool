package net.tc.stresstool.exceptions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

public class StressToolConfigurationException extends StressToolException {
	
	public StressToolConfigurationException() {
		// TODO Auto-generated constructor stub
	}
	public StressToolConfigurationException(Exception e) {
		StringBuffer sb = new StringBuffer();
		ByteArrayOutputStream error = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(error);
		e.printStackTrace(ps);

		new StressToolConfigurationException(error.toString());
		// TODO Auto-generated constructor stub
		
	}
	public StressToolConfigurationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public StressToolConfigurationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public StressToolConfigurationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
}
