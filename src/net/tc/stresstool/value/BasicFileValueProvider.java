/**
 * 
 */
package net.tc.stresstool.value;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tc.data.db.DataType;
import net.tc.utils.SynchronizedMap;
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
    Map <String,Integer> wordsMap =null; 
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
	 Pattern p3 = Pattern.compile("'");
	wordsMap = new SynchronizedMap(0); 
	for(int i = 0 ; i < txtFile.length; i++){
	  if(txtFile[i] != null){
		Matcher m = p.matcher(txtFile[i]); 
    	String toClean =  m.replaceAll("");
    	m = p.matcher(toClean);
    	toClean =  m.replaceAll("");
    	m = p2.matcher(toClean);
    	toClean =  m.replaceAll("");
    	m = p3.matcher(toClean);
    	toClean =  m.replaceAll("");
//    	addWords(toClean);    	
    	txtFile[i] = toClean;
	  }
	}
	sr = null;
	return true;
    }

    private void addWords(String toClean) {
  		String[] words = toClean.split(" ");
  		for(String word:words){
  		  if(wordsMap.containsKey(word.trim())){
  			wordsMap.put(word.trim(),wordsMap.get(word.trim()).intValue()+1);
  		  }
  		  else
  			wordsMap.put(word.trim(),1);
  		}
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
      int textPos =0;
      long seed = Utility.getNumberFromRandomMinMax(1, 100);
      try {
    		textPos = new Long(Utility.getNumberFromRandomMinMax(1, (txtFile.length)/seed)).intValue();
      }
      catch(java.lang.ArithmeticException ax){
    	ax.printStackTrace();
    	
      }
      
      if(!Utility.isPositiveInt(upperbound))
    	  upperbound = (upperbound *-1);
      
      int upto=0;
      if(length > 0 && length < upperbound )
    	  upto= length;
      else
    	  upto = upperbound;
      
      upto=upto>txtFile.length?txtFile.length:upto;
      
//      int upto = length>upperbound?upperbound:length;
     
//      sb.append("\"");
      try{
	      while (sb.length() < upto)
	      {
	    	  if(textPos >= txtFile.length)
	    		  textPos =0;
	    	  sb.append(txtFile[textPos++]);
	    	  if(sb.length() > upto)
	    		break;
	      }
      }catch(Exception ex){ex.printStackTrace();}

      return sb.subSequence(0, upto).toString();
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
