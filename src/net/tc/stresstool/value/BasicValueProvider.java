package net.tc.stresstool.value;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import net.tc.data.db.DataType;
import net.tc.utils.TimeTools;
import net.tc.utils.Utility;

public class BasicValueProvider implements ValueProvider {
  		static final String[] alpha = new String[]{"a","b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","v","z"," "};
  		static final String alphaUTFArabic = "ؠ ء آ أ ؤ إ ئ ا ب ة ت ث ج ح خ د ذ ر ز س ش ص ض ط ظ ع غ ػ ؼ ؽ ؾ ؿ";
  		static final String[] alphaUTFHindy = new String[]{"ऄ","अ ","आ", "इ","ई","उ","ऊ","ऋ","ऌ","ऍ","ऎ","ए","ऐ","ऑ","ऒ","ओ","औ","क","ख","ग","घ","ङ","च","छ","ज","झ","ञ","ट"};
  		static final String[] alphaUTFChinese = new String[]{"表","情","。","另","外","极","少","数","中","文","在","存","储","的","时","候","也","遇","到"};
  		static Random rnd = new Random();  		
  		private Calendar testCalendar = null;
  @Override
  public Long getRandomNumber() {
	 return new Long(rnd.nextInt((int)System.currentTimeMillis()/1000 ));
  }

  @Override
  public Long getRandomNumber(long upperLimit) {
	int min = (int)System.currentTimeMillis()/1000 ;
	int max = new Long(upperLimit).intValue();
	
	if(min == max) return new Long(max);
  	
    if(min == 0 && max == 0){
    	return new Long(0);
    }
    
	Long maxL = new Long(rnd.nextInt(max - min) + min);
	if(maxL < new Long(min)){
		maxL = new Long(min * 2);    		
	}
	return maxL;
  }

  @Override
  public Long getRandomNumber(long lowerLimit, long upperLimit) {
	int min = new Long(lowerLimit).intValue();
	int max = new Long(upperLimit).intValue();
 
	if(min == max) return new Long(max);
  	
    if(min == 0 && max == 0){
    	return new Long(0);
    }
    
	Long maxL = new Long(rnd.nextInt(max - min) + min);
	if(maxL < new Long(min)){
		maxL = new Long(min * 2);    		
	}
	return maxL;
	
  }

 
  @Override
  public ValueProvider copyProvider() {
	// TODO Auto-generated method stub
	return null;
  }

  /**
   * This method return a dataObject with the value set based on the data type
   * A significant switch set will determine what and then will call the relevant implementation
   */
  
  public Object provideValue(DataType dataType, Long length){
    switch (dataType.getDataTypeCategory()){
      case DataType.NUMERIC_CATEGORY:
    	switch (dataType.getDataTypeId()){
	  	case DataType.TINYINT:
	  		return getTiny(length.intValue());
	  	case DataType.SMALLINT:
	  		return getSmallInt(length.intValue());
	  	case DataType.MEDIUMINT:
	  		return getMedInt(length.intValue());
	  	case DataType.INT:
	  		return getInt(length.intValue());
	  	case DataType.BIGINT:
	  		return geBigInt(new Long(length));
	  	case DataType.FLOAT: 	
	  		return getFloat(length.intValue());
	  	case DataType.DOUBLE:
	  		return getDouble(length.intValue());	
	  	case DataType.DECIMAL:
	  		return getDecimal(length.intValue());	  	  
	  	case DataType.BIT:
	  		return getBit(length.intValue());	  	  
    	}

	  	break;
    	
      case DataType.STRING_CATEGORY:
    	switch (dataType.getDataTypeId()){
  	  	case DataType.CHAR:
     	  {
      	    int lowerbound = 0;
      	    long upperbound = 255;
      	    return  getString(upperbound,length.intValue());
     	  }
	  	case DataType.BINARY: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 255;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.VARCHAR: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.VARBINARY: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.TINYBLOB: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 256;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.TINYTEXT: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 256;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.BLOB: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.TEXT: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.MEDIUMBLOB: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 16777216;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.MEDIUMTEXT: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 16777216;
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.LONGBLOB:
       	  {
    	    int lowerbound = 0;
    	    long upperbound = Long.parseLong("4294967296");
    	    return getString(upperbound,length.intValue());
     	  }
	  	case DataType.LONGTEXT:
       	  {
    	    int lowerbound = 0;
    	    long upperbound = Long.parseLong("4294967296");
    	    return getString(upperbound,length.intValue());
     	  }    	  
    	}
    	break;
      case DataType.DATE_CATEGORY:
    	switch (dataType.getDataTypeId()){
    	 case DataType.YEAR: 	
    		  return getYear(length.intValue());
      	case DataType.DATE:
      		return getDate(length.intValue());
      	case DataType.TIME:
      		return getTime(length.intValue());
      	case DataType.DATETIME:
      		return getDateTime(length.intValue());
      	case DataType.TIMESTAMP:
      		return getTimestamp(length.intValue());
      	}
    	break;    	
    
      default: throw new IllegalArgumentException("Invalid data type Category : " + dataType.getDataTypeCategory());
    }
	return null;
	
  }

  @Override
  public String getTimestamp(int length) {
	return TimeTools.GetFullDate(System.currentTimeMillis()); 
  }
  @Override
  public String getDateTime(int daysAdd) {
	if(daysAdd > 0)
	  return TimeTools.GetCurrent(TimeTools.getCalendarFromCalendarDateAddDays(this.testCalendar,daysAdd ));
	
	return null;
	
  }
  @Override
  public String getTime(int addTimeInSeconds) {
	  return TimeTools.GetCurrentTime(testCalendar);
  }
  @Override
  public String getDate(int length) {
	  return TimeTools.GetCurrent(testCalendar);
  }
  @Override
  public String getYear(int length) {
	  return TimeTools.getYear(testCalendar);
  }
  @Override
  public Byte getBit(int length) {
	return getTiny(length).byteValue();
  }
  @Override
  public BigDecimal getDecimal(int length) {
	
	int number1 = getInt(length);
	int number2 = getInt(length);
	DecimalFormat numberFormat = new DecimalFormat("#.00000");
	return new BigDecimal(numberFormat.format(number1/number2));	
  }
  @Override
  public Double getDouble(int length) {
	double myDouble = length;
	return new Double(myDouble * (getInt(length)/100));
	
  }
  @Override
  public Float getFloat(int length) {
	float myfloat = length;
	return new Float(myfloat * (getInt(length)/100));
	 	
  }
  @Override
  public Long geBigInt(Long length) {
	return this.getRandomNumber(length>Long.MAX_VALUE?Long.MAX_VALUE:length);
	
  }
  @Override
  public Integer getInt(int length) {
	return this.getRandomNumber(length>2147483647?2147483647:length).intValue();
	// TODO Auto-generated method stub
	
  }
  @Override
  public Integer getMedInt(int length) {
	return this.getRandomNumber(length>8388607?8388607:length).intValue();
	// TODO Auto-generated method stub
	
  }
  @Override
  public Integer getSmallInt(int length) {
	return this.getRandomNumber(length>32767?32767:length).intValue();
	// TODO Auto-generated method stub
	
  }
  @Override
  public Integer getTiny(int length) {
	return this.getRandomNumber(length>127?127:length).intValue();
	// TODO Auto-generated method stub
	
  }

  @Override
  public Date getRandomDate() {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public boolean readText(String path, int splitMethod) {
	// TODO Auto-generated method stub
	return false;
  }
//	  	
//

  @Override
  public String getString(int length) {
	return this.getString(65535, length);
  }

  @Override
  public String getStringUTFArabic(int length) {
    StringBuffer sb = new StringBuffer();

    for (int i = 0 ; i <= length ; i++ )
    {
        sb.append(alphaUTFArabic);
    }

    return sb.toString().substring(0, length);
  }
  
  @Override
  public String getStringUTFHindi(int length) {
    StringBuffer sb = new StringBuffer();

    for (int i = 0 ; i <= length ; i++ )
    {
      sb.append(alphaUTFHindy[rnd.nextInt(alpha.length)]);
    }

    return sb.toString().substring(0, length);
  }

  @Override
  public String getStringUTFChinese(int length) {
    StringBuffer sb = new StringBuffer();

    for (int i = 0 ; i <= length ; i++ )
    {
      sb.append(alphaUTFChinese[rnd.nextInt(alpha.length)]);
    }

    return sb.toString().substring(0, length);
  }
  
  @Override
  public String getString(int upperbound, int length) {
    StringBuffer sb = new StringBuffer();

    for (int i = 0 ; i <= length ; i++ )
    {
        sb.append(alpha[rnd.nextInt(alpha.length)]);
    }
    return sb.subSequence(0, upperbound).toString();
  }

  @Override
  public String getString(long upperbound, int length) {
    StringBuffer sb = new StringBuffer();

    for (int i = 0 ; i <= length ; i++ )
    {
        sb.append(alpha[rnd.nextInt(alpha.length)]);
    }
    return sb.subSequence(0, sb.length()>upperbound?new Long(upperbound).intValue():sb.length()).toString();
  }


  @Override
  public Long getRandomNumberIndex(int index)
  {
//  	System.out.println(index);
      return new Long(rnd.nextInt(index));

  }


  @Override
  public Long getRandomNumberMinMaxCeling(int min,int max,int celing)
  {
  	if(min == max) return new Long(max);
  	
      if(min == 0 && max == 0){
      	return new Long(0);
      }
      
  	Long maxL = new Long(rnd.nextInt(max));
  	if(maxL < new Long(min)){
  		maxL = new Long(min * 2);    		
  	}
  	
  	if((maxL - new Long(min)) > celing){
  		return Math.abs(new Long (min + celing));
  	}
  	return Math.abs(maxL);

  }

  /**
   * @return the testCalendar
   */
  @Override
  public Calendar getTestCalendar() {
    return testCalendar;
  }

  /**
   * @param testCalendar the testCalendar to set
   */
  @Override
  public void setTestCalendar(Calendar testCalendar) {
    this.testCalendar = testCalendar;
  }

@Override
public Calendar resetCalendar(int timeDays) {
	int days = Utility.getNumberFromRandomMinMax((timeDays * -1), timeDays).intValue();
	testCalendar = TimeTools.getCalendarFromCalendarDateAddDays(testCalendar, days);
	 
	return testCalendar;
}


 
}
