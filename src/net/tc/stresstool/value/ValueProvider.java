package net.tc.stresstool.value;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import net.tc.data.db.DataType;

public interface ValueProvider {
    
    public static int SPLIT_METHOD_CSV = 1;
    public static int SPLIT_METHOD_TAB = 2;
    public static int SPLIT_METHOD_SPACE = 3;
    public static int SPLIT_METHOD_UNIX_END_LINE = 4;
    public static int SPLIT_METHOD_WINDOWS_END_LINE = 5;
    
//    public String getValueTextFromRandom(int length);
//    public String getValueTextFromText(int length);
//    public String getValueTextFromText(int lowerLimit,int length);
//    public String[] getValueTextFromText(int upperLimit,int lowerLimit,int length);
    
    public Long getRandomNumber();
    public Long getRandomNumber(long upperLimit);
    public Long getRandomNumber(long lowerLimit, long upperLimit);
    public Date getRandomDate();
    public abstract ValueProvider copyProvider();
    public Object provideValue(DataType dataType, Long length);
    public boolean readText(String path, int splitMethod);
    public String getTimestamp(int length);
    public String getDateTime(int length);
    public String getTime(int length);
    public String getYear(int length);
    public String getDate(int length);
    public Byte getBit(int length);
    public BigDecimal getDecimal(int length);
    public Double getDouble(int length);
    public Float getFloat(int length);
    public Long geBigInt(Long length);
    public Integer getMedInt(int length);
    public Integer getSmallInt(int length);
    public Integer getTiny(int length);
    public String getString(int length);
    public String getString(int upperbound, int length);
    public String getString(long upperbound, int length);
    public Long getRandomNumberMinMaxCeling(int min, int max, int celing);
    public Long getRandomNumberIndex(int index);
    public String getStringUTFArabic(int length);
    public String getStringUTFHindi(int length);
    public String getStringUTFChinese(int length);
	public Integer getInt(int length);
//	public Calendar getTestCalendar();
//	public void setTestCalendar(Calendar testCalendar);
	public Calendar resetCalendar(int timeDays);
    
}
