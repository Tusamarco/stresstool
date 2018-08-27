package net.tc.data.db;

public class DataType {
/**
 * NOTE 
 * DataType class is a map SQL/Java and it should be standard cross DBTYPE
 */
    public static final int NUMERIC_CATEGORY = 1;
    public static final int STRING_CATEGORY = 2;
    public static final int DATE_CATEGORY = 3;
    
    public static final int TINYINT = 0;	
    public static final int SMALLINT	= 1;
    public static final int MEDIUMINT	= 2;
    public static final int INT = 3;
    public static final int INTEGER = 3;
    public static final int BIGINT  = 4;		
    public static final int FLOAT = 5	;
    public static final int DOUBLE = 6 ;	 
    public static final int DECIMAL = 7 ;	
    public static final int BIT = 8 ;	 	
    public static final int  YEAR = 9; 		
    public static final int  DATE = 10;		
    public static final int  TIME = 11;		
    public static final int  DATETIME	= 12 ;
    public static final int  TIMESTAMP = 13;	
    public static final int  CHAR =14 ; 		
    public static final int  BINARY = 15 ; 
    public static final int  VARCHAR =16 ;
    public static final int  VARBINARY =116 ;
    public static final int  TINYBLOB	=17 ;
    public static final int  TINYTEXT	=18;
    public static final int  BLOB	=19;	
    public static final int  TEXT	=20;	
    public static final int  MEDIUMBLOB = 21;
    public static final int  MEDIUMTEXT =22 ;
    public static final int  LONGBLOB	 = 23 ;
    public static final int  LONGTEXT	=24 ;
    public static final int  ENUM	=25 ;	
    public static final int  SET	=26 ;
    
    private int dataTypeId = 6666;
    private int storageBytes = 0 ;
    private int dataTypeCategory = 0;
    
    /*
    0  TINYINT	    1
    1  SMALLINT	2
    2  MEDIUMINT	3
    3  INT|INTEGER	4
    4  BIGINT		8
    5  FLOAT(p)	8
    6  FLOAT		4
    7  DOUBLE 		8 
    8  DECIMAL		4
    9  BIT			1
    10  YEAR		1 
    11  DATE		3
    12  TIME		3
    13  DATETIME	8
    14  TIMESTAMP	4
    15  CHAR		0
    16  BINARY      0
    17  VARCHAR     0
    18  TINYBLOB	0
    19  TINYTEXT	0
    20  BLOB		0
    21  TEXT		0
    22  MEDIUMBLOB	0
    23  MEDIUMTEXT	0
    24  LONGBLOB	0
    25  LONGTEXT	0
    26  ENUM		0
    27  SET			0
 */
    public DataType(int dataTypeIdentifier) {
	switch (dataTypeIdentifier){
        	case DataType.TINYINT:
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.TINYINT);
        	    this.setStorageBytes(1);
        	    break; 
        	case DataType.SMALLINT:
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.SMALLINT);
        	    this.setStorageBytes(2);
        	    break; 
 
        	case DataType.MEDIUMINT:
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.MEDIUMINT);
        	    this.setStorageBytes(3);
        	    break; 

        	case DataType.INT:
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.INT);
        	    this.setStorageBytes(4);
        	    break; 

        	case DataType.BIGINT:
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.BIGINT);
        	    this.setStorageBytes(8);
        	    break; 

        	case DataType.FLOAT: 	
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.FLOAT);
        	    this.setStorageBytes(4);
        	    break; 

        	case DataType.DOUBLE:
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.DOUBLE);
        	    this.setStorageBytes(8);
        	    break; 

        	case DataType.DECIMAL: 
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.DECIMAL);
        	    this.setStorageBytes(4);
        	    break;
        	case DataType.BIT:
        	    this.setDataTypeCategory(NUMERIC_CATEGORY);
        	    this.setDataTypeId(DataType.BIT);
        	    this.setStorageBytes(1);
        	    break;
        	case DataType.YEAR: 	
        	    this.setDataTypeCategory(DataType.DATE_CATEGORY);
        	    this.setDataTypeId(DataType.YEAR);
        	    this.setStorageBytes(1);
        	    break;
        	case DataType.DATE:
        	    this.setDataTypeCategory(DataType.DATE_CATEGORY);
        	    this.setDataTypeId(DataType.DATE);
        	    this.setStorageBytes(3);
        	    break;

        	case DataType.TIME: 	
        	    this.setDataTypeCategory(DataType.DATE_CATEGORY);
        	    this.setDataTypeId(DataType.TIME);
        	    this.setStorageBytes(3);
        	    break;

        	case DataType.DATETIME: 
        	    this.setDataTypeCategory(DataType.DATE_CATEGORY);
        	    this.setDataTypeId(DataType.DATETIME);
        	    this.setStorageBytes(8);
        	    break;

        	case DataType.TIMESTAMP: 	
        	    this.setDataTypeCategory(DataType.DATE_CATEGORY);
        	    this.setDataTypeId(DataType.TIMESTAMP);
        	    this.setStorageBytes(4);
        	    break;

        	case DataType.CHAR: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.CHAR);
        	    this.setStorageBytes(0);
        	    break;

        	case DataType.BINARY: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.BINARY);
        	    this.setStorageBytes(0);
        	    break;
        	    
        	case DataType.VARCHAR: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.VARCHAR);
        	    this.setStorageBytes(1);
        	    break;
        	case DataType.VARBINARY: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.VARBINARY);
        	    this.setStorageBytes(1);
        	    break;

        	case DataType.TINYBLOB: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.TINYBLOB);
        	    this.setStorageBytes(1);
        	    break;
        	case DataType.TINYTEXT: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.TINYTEXT);
        	    this.setStorageBytes(1);
        	    break;
        	case DataType.BLOB: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.BLOB);
        	    this.setStorageBytes(2);
        	    break;
        	case DataType.TEXT: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.TEXT);
        	    this.setStorageBytes(2);
        	    break;
        	case DataType.MEDIUMBLOB: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.MEDIUMBLOB);
        	    this.setStorageBytes(3);
        	    break;
        	case DataType.MEDIUMTEXT: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.MEDIUMTEXT);
        	    this.setStorageBytes(3);
        	    break;
        	case DataType.LONGBLOB:
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.LONGBLOB);
        	    this.setStorageBytes(4);
        	    break;
        	case DataType.LONGTEXT:
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.LONGTEXT);
        	    this.setStorageBytes(4);
        	    break;
        	case DataType.ENUM: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.ENUM);
        	    this.setStorageBytes(1);
        	    break;
        	case DataType.SET: 
        	    this.setDataTypeCategory(DataType.STRING_CATEGORY);
        	    this.setDataTypeId(DataType.SET);
        	    this.setStorageBytes(1);
        	    break;
	default: throw new IllegalArgumentException("Invalid data type index: " + dataTypeIdentifier);
	}
	
	
	
	
	// TODO Auto-generated constructor stub
    }	
    public static int getDataTypeIdentifierByString(String datatypeString){
	if(datatypeString !=null 
	   && !datatypeString.equals("")){
        	switch (datatypeString.toUpperCase()){
        	case "TINYINT"	: return DataType.TINYINT; 
        	case "SMALLINT"	: return DataType.SMALLINT; 
        	case "MEDIUMINT" : return DataType.MEDIUMINT;	
        	case "INT" : return DataType.INT;
        	case "INTEGER" : return DataType.INT;
        	case "BIGINT"	: return DataType.BIGINT; 	
        	case "FLOAT"	: return DataType.FLOAT; 	
        	case "DOUBLE" 	: return DataType.DOUBLE;  
        	case "DECIMAL"	: return DataType.DECIMAL; 
        	case "BIT"	: return DataType.BIT; 	
        	case "YEAR"	: return DataType.YEAR; 	
        	case "DATE"	: return DataType.DATE; 
        	case "TIME"	: return DataType.TIME; 	
        	case "DATETIME" : return DataType.DATETIME; 
        	case "TIMESTAMP" : return DataType.TIMESTAMP; 	
        	case "CHAR" : return DataType.CHAR; 
        	case "BINARY"    : return DataType.BINARY; 
        	case "VARCHAR"   : return DataType.VARCHAR;
        	case "VARBINARY" : return DataType.VARBINARY;
        	case "TINYBLOB"	: return DataType.TINYBLOB; 
        	case "TINYTEXT" : return DataType.TINYTEXT; 
        	case "BLOB"	: return DataType.BLOB; 
        	case "TEXT"	: return DataType.TEXT; 
        	case "MEDIUMBLOB" : return DataType.MEDIUMBLOB; 
        	case "MEDIUMTEXT" : return DataType.MEDIUMTEXT; 
        	case "LONGBLOB" : return DataType.LONGBLOB; 
        	case "LONGTEXT" : return DataType.LONGTEXT; 
        	case "ENUM" : return DataType.ENUM; 
        	case "SET"	: return DataType.SET; 
        	default: throw new IllegalArgumentException("Invalid data type: " + datatypeString.toUpperCase());
        	
        	}
        	
	}
	
	
	return 6666;
    }

    public static String getDataTypeStringByIdentifier(int dataTypeIdentifier){
	switch (dataTypeIdentifier){
    	case DataType.TINYINT: return "TINYINT";
	case DataType.SMALLINT:return "SMALLINT";
	case DataType.MEDIUMINT:return "MEDIUMINT";
	case DataType.INT: return "INT";
	case DataType.BIGINT:return "BIGINT";
	case DataType.FLOAT: return "FLOAT";
	case DataType.DOUBLE: return "DOUBLE";
	case DataType.DECIMAL: return "DECIMAL";
	case DataType.BIT: return "BIT";
	case DataType.YEAR: return "YEAR";
	case DataType.DATE: return "DATE";
	case DataType.TIME: return "TIME";
	case DataType.DATETIME: return "DATETIME";
	case DataType.TIMESTAMP: return "TIMESTAMP";
	case DataType.CHAR: return "CHAR";
	case DataType.BINARY: return "BINARY";	    
	case DataType.VARCHAR: return "VARCHAR";
	case DataType.VARBINARY: return "VARBINARY";
	case DataType.TINYBLOB: return "TINYBLOB";
	case DataType.TINYTEXT: return "TINYTEXT";
	case DataType.BLOB: return "BLOB"; 
	case DataType.TEXT: return "TEXT"; 
	case DataType.MEDIUMBLOB: return "MEDIUMBLOB"; 
	case DataType.MEDIUMTEXT: return "MEDIUMTEXT";
	case DataType.LONGBLOB: return "LONGBLOB";
	case DataType.LONGTEXT: return "LONGTEXT";
	case DataType.ENUM: return "ENUM";
	case DataType.SET: return "SET";
	default: throw new IllegalArgumentException("Invalid data type index: " + dataTypeIdentifier);
        	
        }
    }

        
    
    /**
     * @return the dataTypeId
     */
    public int getDataTypeId() {
        return dataTypeId;
    }
    /**
     * @param dataTypeId the dataTypeId to set
     */
    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }
    /**
     * @return the storageBytes
     */
    public int getStorageBytes() {
        return storageBytes;
    }
    /**
     * @param storageBytes the storageBytes to set
     */
    public void setStorageBytes(int storageBytes) {
        this.storageBytes = storageBytes;
    }
    /**
     * @return the dataTypeCategory
     */
    public int getDataTypeCategory() {
        return dataTypeCategory;
    }
    /**
     * @param dataTypeCategory the dataTypeCategory to set
     */
    public void setDataTypeCategory(int dataTypeCategory) {
        this.dataTypeCategory = dataTypeCategory;
    }
 
    public static String getClassFromDataType(int dataTypeIdentifier){
      
      	switch (dataTypeIdentifier){
      	case DataType.TINYINT: return "java.lang.Integer";
    	case DataType.SMALLINT:return "java.lang.Integer";
    	case DataType.MEDIUMINT:return "java.lang.Integer";
    	case DataType.INT: return "java.lang.Integer";
    	case DataType.BIGINT:return "java.lang.Long";
    	case DataType.FLOAT: return "java.lang.String";
    	case DataType.DOUBLE: return "java.lang.Double";
    	case DataType.DECIMAL: return "java.math.BigDecimal";
    	case DataType.BIT: return "java.lang.Integer";
    	case DataType.YEAR: return "java.lang.Integer";
    	case DataType.DATE: return "java.lang.String";
    	case DataType.TIME: return "java.lang.String";
    	case DataType.DATETIME: return "java.sql.Date";
    	case DataType.TIMESTAMP: return "java.sql.Timestamp";
    	case DataType.CHAR: return "java.lang.String";
    	case DataType.BINARY: return "byte[]";	    
    	case DataType.VARCHAR: return "java.lang.String";
    	case DataType.VARBINARY: return "byte[]";
    	case DataType.TINYBLOB: return "byte[]";
    	case DataType.TINYTEXT: return "TINYTEXT";
    	case DataType.BLOB: return "byte[]"; 
    	case DataType.TEXT: return "java.lang.String"; 
    	case DataType.MEDIUMBLOB: return "byte[]"; 
    	case DataType.MEDIUMTEXT: return "java.lang.String";
    	case DataType.LONGBLOB: return "byte[]";
    	case DataType.LONGTEXT: return "java.lang.String";
    	case DataType.ENUM: return "java.lang.String";
    	case DataType.SET: return "java.lang.String";
    	default: throw new IllegalArgumentException("Invalid data type index: " + dataTypeIdentifier);
      	
      }
      
      
    }
    
}
