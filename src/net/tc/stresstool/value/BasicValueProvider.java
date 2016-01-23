package net.tc.stresstool.value;

import java.math.BigDecimal;
import java.util.Date;

import net.tc.data.db.DataType;

public class BasicValueProvider implements ValueProvider {

  @Override
  public String getValueTextFromRandom(int length) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public String getValueTextFromText(int length) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public String getValueTextFromText(int lowerLimit, int length) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public String[] getValueTextFromText(int upperLimit, int lowerLimit, int length) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public Long getRandomNumber() {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public Long getRandomNumber(long upperLimit) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public Long getRandomNumber(long lowerLimit, long upperLimit) {
	// TODO Auto-generated method stub
	return null;
  }

  @Override
  public String getText(int lenght) {
	// TODO Auto-generated method stub
	return null;
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
  
  public Object provideValue(DataType dataType, int length){
    switch (dataType.getDataTypeCategory()){
      case DataType.NUMERIC_CATEGORY:
    	switch (dataType.getDataTypeId()){
	  	case DataType.TINYINT:
	  	  	this.getTiny(length);
	  	    break; 
	  	
	  	case DataType.SMALLINT:
	  	  this.getSmallInt(length);
	  	  break; 
  	  	
	  	case DataType.MEDIUMINT:
	  	  this.getMedInt(length);
	  	  break; 

	  	case DataType.INT:
	  	  this.geInt(length);
	  	  break; 

	  	case DataType.BIGINT:
	  	  this.geBigInt(length);
	  	  break; 

	  	case DataType.FLOAT: 	
	  	  this.getFloat(length);
	  	  break; 

	  	case DataType.DOUBLE:
	  	  this.getDouble(length);	
	  	  break; 

	  	case DataType.DECIMAL:
	  	  this.getDecimal(length);	  	  
	  	  break;
	  	
	  	case DataType.BIT:
	  	  this.getBit(length);	  	  
	  	  break;
    	}

	  	break;
    	
      case DataType.STRING_CATEGORY:
    	switch (dataType.getDataTypeId()){
  	  	case DataType.CHAR:
     	  {
      	    int lowerbound = 0;
      	    long upperbound = 255;
      	    this.getString(lowerbound,upperbound,length);
     	  }
   	  	  break;
	  	case DataType.BINARY: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 255;
    	    this.getString(lowerbound,upperbound,length);
     	  }
	  	  break;
	  	    
	  	case DataType.VARCHAR: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    this.getString(lowerbound,upperbound,length);
     	  }
	  	  break;

	  	case DataType.VARBINARY: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    this.getString(lowerbound,upperbound,length);
     	  }
	  	  break;

	  	case DataType.TINYBLOB: 
     	  {
    	    int lowerbound = 0;
    	    long upperbound = 256;
    	    this.getString(lowerbound,upperbound,length);
     	  }
	  	  break;
	 
	  	case DataType.TINYTEXT: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 256;
    	    this.getString(lowerbound,upperbound,length);
     	  }
	  	  break;
	 
	  	case DataType.BLOB: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    this.getString(lowerbound,upperbound,length);
     	  }
	  	  break;

	  	case DataType.TEXT: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 65535;
    	    this.getString(lowerbound,upperbound,length);
     	  }
	  	  break;

	  	case DataType.MEDIUMBLOB: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 16777216;
    	    this.getString(lowerbound,upperbound,length);
     	  }

	  	  break;

	  	case DataType.MEDIUMTEXT: 
       	  {
    	    int lowerbound = 0;
    	    long upperbound = 16777216;
    	    this.getString(lowerbound,upperbound,length);
     	  }

	  	  break;
	  	
	  	case DataType.LONGBLOB:
       	  {
    	    int lowerbound = 0;
    	    long upperbound = Long.parseLong("4294967296");
    	    this.getString(lowerbound,upperbound,length);
     	  }

	  	  break;
	 
	  	case DataType.LONGTEXT:
       	  {
    	    int lowerbound = 0;
    	    long upperbound = Long.parseLong("4294967296");
    	    this.getString(lowerbound,upperbound,length);
     	  }

	  	  break;

    	  
    	}
    	break;
      case DataType.DATE_CATEGORY:
    	switch (dataType.getDataTypeId()){
    	  	case DataType.YEAR: 	
    	  	  this.getYear(length);
    	  	  break;
    
      	case DataType.DATE:
      	  this.getDate(length);
      	  break;
    
      	case DataType.TIME:
      	  this.getTime(length);
      	  break;
    
      	case DataType.DATETIME:
      	  this.getDateTime(length);
      	  break;
    
      	case DataType.TIMESTAMP:
      	  this.getTimestamp(length);
      	  break;
      	}
    	
    	break;
    	
      default: throw new IllegalArgumentException("Invalid data type Category : " + dataType.getDataTypeCategory());
    	
    }
	
    
	return null;
	
  }
  @Override
  public  String getString(int lowerbound, long upperbound, int length) {
	return null;
	// TODO Auto-generated method stub
	
  }

  @Override
  public String getTimestamp(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public String getDateTime(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public String getTime(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public String getDate(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public String getYear(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Byte getBit(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public BigDecimal getDecimal(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Double getDouble(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Float getFloat(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Long geBigInt(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Integer geInt(int length) {
	return length;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Integer getMedInt(int length) {
	return length;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Integer getSmallInt(int length) {
	return null;
	// TODO Auto-generated method stub
	
  }
  @Override
  public Integer getTiny(int length) {
	return length;
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

//	  	case DataType.ENUM: 
//	  	    break;
//
//	  	case DataType.SET: 
//	  	    break;
//	  	default: throw new IllegalArgumentException("Invalid data type index: " + dataType.getDataTypeId());


}
