package net.tc.stresstool.value;

public interface ValueProvider {
    
    public static int SPLIT_METHOD_CSV = 1;
    public static int SPLIT_METHOD_TAB = 2;
    public static int SPLIT_METHOD_SPACE = 3;
    public static int SPLIT_METHOD_UNIX_END_LINE = 4;
    public static int SPLIT_METHOD_WINDOWS_END_LINE = 5;
    
    public String getValueTextFromRandom(int length);
    public String getValueTextFromText(int length);
    public String getValueTextFromText(int lowerLimit,int length);
    public String[] getValueTextFromText(int upperLimit,int lowerLimit,int length);
    
    public Long getRandomLong();
    public Long getRandomLong(long upperLimit);
    public Long getRandomLong(long lowerLimit, long upperLimit);
    
    public boolean readText(String Path, int splitMethod); 
    public abstract ValueProvider copyProvider();
    
}
