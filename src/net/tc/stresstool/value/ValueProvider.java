package net.tc.stresstool.value;

import java.math.BigDecimal;
import java.util.Date;

import net.tc.data.db.DataType;

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
    
    public Long getRandomNumber();
    public Long getRandomNumber(long upperLimit);
    public Long getRandomNumber(long lowerLimit, long upperLimit);
    public Date getRandomDate();
    public String getText(int lenght); 
    public abstract ValueProvider copyProvider();
    public Object provideValue(DataType dataType, int length);
	boolean readText(String path, int splitMethod);
	String getTimestamp(int length);
	String getDateTime(int length);
	String getTime(int length);
	String getYear(int length);
	String getDate(int length);
	Byte getBit(int length);
	BigDecimal getDecimal(int length);
	Double getDouble(int length);
	Float getFloat(int length);
	Long geBigInt(int length);
	Integer geInt(int length);
	Integer getMedInt(int length);
	Integer getSmallInt(int length);
	Integer getTiny(int length);
	String getString(int lowerbound, int upperbound, int length);
	String getString(int lowerbound, long upperbound, int length);
    
}
