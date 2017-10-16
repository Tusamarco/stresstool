package net.tc.stresstool;

public class PerformanceEvaluator {

    public PerformanceEvaluator() {
	// TODO Auto-generated constructor stub
    }

    public static String getTimeEvaluation(long performanceTimeStart){
	     long performanceTimeEnd = System.nanoTime();
	     long nano = (performanceTimeEnd - performanceTimeStart);
	     return nano + " ns (nano seconds) [ " +(nano/1000000)+" ms (milliseconds)]";
	
    } 
    
    public static long getTimeEvaluationMs(long performanceTimeStart){
	     long performanceTimeEnd = System.nanoTime();
	     long nano = (performanceTimeEnd - performanceTimeStart);
	     return (nano/1000000);
	
   } 
    public static long getMSFromNamo(long performanceTimeStart){
	     return (performanceTimeStart/1000000);
	
  } 
    
    
}
