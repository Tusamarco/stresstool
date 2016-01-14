package net.tc.data.db;

public class DataType {

    public static int NUMERIC_CATEGORY = 1;
    public static int STRING_CATEGORY = 2;
    public static int DATE_CATEGORY = 3;
    
    public static int TINYINT = 0;	
    public static int SMALLINT	= 1;
    public static int MEDIUMINT	= 2;
    public static int INT = 3;
    public static int INTEGER = 3;
    public static int BIGINT  = 4;		
    public static int FLOAT = 5	;
    public static int DOUBLE = 6 ;	 
    public static int DECIMAL = 7 ;	
    public static int BIT = 8 ;	 	
    public static int  YEAR = 9; 		
    public static int  DATE = 10;		
    public static int  TIME = 11;		
    public static int  DATETIME	= 12 ;
    public static int  TIMESTAMP = 13;	
    public static int  CHAR =14 ; 		
    public static int  BINARY = 15 ; 
    public static int  VARCHAR =16 ;  
    public static int  TINYBLOB	=17 ;
    public static int  TINYTEXT	=18;
    public static int  BLOB	=19;	
    public static int  TEXT	=20;	
    public static int  MEDIUMBLOB = 21;
    public static int  MEDIUMTEXT =22 ;
    public static int  LONGBLOB	 = 23 ;
    public static int  LONGTEXT	=24 ;
    public static int  ENUM	=25 ;	
    public static int  SET	=26 ;
    
    private int dataTypeId = 6666;
    private int storageBytes = 0 ;
    private int dataTypeCategory = 0;
    
    
    public DataType(int dataTypeIdentifier) {
	switch (dataTypeIdentifier){
        	case DataType.TINYINT:; 
        	case DataType.SMALLINT; 
        	case DataType.MEDIUMINT;	
        	case DataType.INT;
        	case DataType.BIGINT; 	
        	case DataType.FLOAT; 	
        	case DataType.DOUBLE;  
        	case DataType.DECIMAL; 
        	case DataType.BIT; 	
        	case DataType.YEAR; 	
        	case DataType.DATE; 
        	case DataType.TIME; 	
        	case DataType.DATETIME; 
        	case DataType.TIMESTAMP; 	
        	case DataType.CHAR; 
        	case DataType.BINARY; 
        	case DataType.VARCHAR; 
        	case DataType.TINYBLOB; 
        	case DataType.TINYTEXT; 
        	case DataType.BLOB; 
        	case DataType.TEXT; 
        	case DataType.MEDIUMBLOB; 
        	case DataType.MEDIUMTEXT; 
        	case DataType.LONGBLOB; 
        	case DataType.LONGTEXT; 
        	case DataType.ENUM; 
        	case DataType.SET; 
	default: throw new IllegalArgumentException("Invalid data type: " + datatypeString.toUpperCase());
	}
	
	
	
	super();
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
    
}
