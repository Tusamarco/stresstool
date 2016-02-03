/**
 * 
 */
package net.tc.stresstool.value;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tc.data.db.DataType;
import net.tc.utils.Utility;
import net.tc.utils.file.FileHandler;

/**
 * @author tusa
 *
 */
public class BasicFileValueProvider extends BasicValueProvider implements ValueProvider {

//    private static final long upperbound = 0;

	public BasicFileValueProvider() {
    }

    public BasicFileValueProvider(String path) {
	super();
	this.path = path;
    }

    String path = null;
    String[] txtFile = null;
    int position = 0;
    
    /*Return a random entry from the array limited by the Length 
     */
    
    public String getValueTextFromRandom(int length) {
	if(length < 1 )
	    return null;

	return txtFile[Utility.getNumberFromRandomMinMax(0, txtFile.length).intValue()].substring(0, length);
	
    }

    /* Return consecutive values from the array entry starting from 0 and incrementing 
     * the position at each call, when it reach the max length it starts from 0 again
     */
    
    public String getValueTextFromText(int length) {
	if(length < 1 )
	    return null;
	
	return txtFile[position>txtFile.length?position=0:position++].substring(0, length);
	
    }

    /* Return values from the array entry starting from lowerLimit 
     * 
     */
    
    public String getValueTextFromText(int lowerLimit, int length) {
	if(length < 1 )
	    return null;
	
	return txtFile[lowerLimit>txtFile.length?lowerLimit=(txtFile.length -1):lowerLimit].substring(0, length);
    }

    /* Return consecutive values from the array entry starting from lowerLimit 
     * up to the upperLimit
     */
    
    public String[] getValueTextFromText(int upperLimit, int lowerLimit, int length) {
    	if(length < 1 || upperLimit == 0 || upperLimit < lowerLimit)
    	    return null;

    	String[] values = new String[(upperLimit - lowerLimit)];
    	for(int i = lowerLimit; i <= upperLimit; i++){
    	    values[i] = txtFile[i].substring(0, length);
    	}
    	return values;
    }

    /* Return a single Long value  
     */
    @Override
    public Long getRandomNumber() {
	return Utility.getNumberFromRandom(new Long(System.currentTimeMillis()).intValue());
    }

    /* Return a long no bigger than upperLimit
     */
    @Override
    public Long getRandomNumber(long upperLimit) {
	return Utility.getNumberFromRandomMinMax(0, new Long(upperLimit).intValue()); 
	
    }

    /* Return a long value between limits
     */
    @Override
    public Long getRandomNumber(long lowerLimit, long upperLimit) {
	return Utility.getNumberFromRandomMinMax(lowerLimit, upperLimit);
	}

    /* 
     * Read the text in the file and load an object of String[] type to be used for data generation.
     * Each entry in the array is a line coming from the file, no truncation
     */
    @Override
    public boolean readText(String path, int splitMethod) {
	if(path == null || path.equals(""))
	    return false;
	this.path = path;
	FileHandler fileH = null;
	SoftReference sr = new SoftReference(fileH = new FileHandler(path,FileHandler.FILE_FOR_READ));
	((FileHandler)sr.get()).readInitialize(false);
	
	txtFile = ((FileHandler)sr.get()).getTextFileAsStringArray();
	 Pattern p = Pattern.compile("\\s\\s");
	 Pattern p2 = Pattern.compile("\"");
	for(int i = 0 ; i < txtFile.length; i++){
	  if(txtFile[i] != null){
		Matcher m = p.matcher(txtFile[i]); 
    	String toClean =  m.replaceAll("");
    	m = p.matcher(toClean);
    	toClean =  m.replaceAll("");
    	txtFile[i] = toClean;
	  }
	}
	sr = null;
	return true;
    }

//    
//    public String getText(int lenght){
//	  return path;
//      
//    }
    public ValueProvider copyProvider(){
	if(this.txtFile != null && this.txtFile.length > 0){
	    BasicFileValueProvider vp = new BasicFileValueProvider();
	    vp.setPath(this.getPath());
	    vp.setTxtFile(Arrays.copyOf(this.getTxtFile(), this.getTxtFile().length));
	    return vp;
	}
	
	return null;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the txtFile
     */
    String[] getTxtFile() {
        return txtFile;
    }

    /**
     * @param txtFile the txtFile to set
     */
    void setTxtFile(String[] txtFile) {
        this.txtFile = txtFile;
    }
    
    @Override
    public String getString(int upperbound, int length) {
      StringBuffer sb = new StringBuffer();
      
      int textPos = new Long(Utility.getNumberFromRandomMinMax((int) (System.currentTimeMillis()/10000), (txtFile.length)/Utility.getNumberFromRandomMinMax(1, 100))).intValue();
      int upto = length>upperbound?upperbound:length;
     
//      sb.append("\"");
      while (sb.length() < upto)
      {
    	  if(textPos == txtFile.length)
    		  textPos =0;
    	  sb.append(txtFile[textPos++]);
      }
      

      return sb.toString().substring(0,upto);
//      + "\"";
  	
    }
    @Override
    public String getString(long upperbound, int length) {
    	return getString(new Long(upperbound).intValue(), length) ;
    }
    @Override
    public String getString(int length) {
      return this.getString(255, length);
    }
}
