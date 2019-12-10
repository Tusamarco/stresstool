package net.tc.stresstool;

public class PerformanceEvaluator {

	public PerformanceEvaluator() {
		// TODO Auto-generated constructor stub
	}

	public static String getTimeEvaluationNs(long performanceTimeStart) {
		long performanceTimeEnd = System.nanoTime();
		long nano = (performanceTimeEnd - performanceTimeStart);
		return nano + " ns (nano seconds) [ " + (nano / 1000000) + " ms (milliseconds)]";

	}

	public static long getTimeEvaluationMs(long performanceTimeStart) {
		long performanceTimeEnd = System.nanoTime();
		long nano = (performanceTimeEnd - performanceTimeStart);
		return (nano / 1000000);

	}
	
	public static long getTimeEvaluationSec(long performanceTimeStart) {
		long performanceTimeEnd = System.nanoTime();
		long nano = (performanceTimeEnd - performanceTimeStart);
		return (nano / 1000000000);

	}	

	public static long getMSFromNano(long performanceTimeStart) {
		return (performanceTimeStart / 1000000);

	}

	public static long getSecFromNano(long performanceTimeStart) {
		return (performanceTimeStart / 1000000000);

	}

	public String getITimeEvaluationNs(long performanceTimeStart) {
		long performanceTimeEnd = System.nanoTime();
		long nano = (performanceTimeEnd - performanceTimeStart);
		return nano + " ns (nano seconds) [ " + (nano / 1000000) + " ms (milliseconds)]";

	}

	public long getITimeEvaluationMs(long performanceTimeStart) {
		long performanceTimeEnd = System.nanoTime();
		long nano = (performanceTimeEnd - performanceTimeStart);
		return (nano / 1000000);

	}

	public long getIMSFromNano(long performanceTimeStart) {
		return (performanceTimeStart / 1000000);

	}
	public double getIMSFromNano(double performanceTimeStart) {
		return (performanceTimeStart / 1000000);

	}
	public long getISecFromNano(long performanceTimeStart) {
		return (performanceTimeStart / 1000000000);

	}

}
