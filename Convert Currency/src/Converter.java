import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;

/**
 * Using this class, we convert currencies to its foreign exchange. This class uses google calculator to perform the conversion process
 * @author Bright Dadson
 */
public final class Converter
{
  private static final String regExp = "-?\\d+(.\\d+)?";
 
  private double valueToConvert;
  private String convertFrom, convertTo;
 
  public Converter()
  {}
 
  /**
   * Convert submitted value from and to the submitted conversion codes.
   * 
   * @param valueToConvert - Amount to convert
   * @param convertFrom - Currency code to convert from
   * @param convertTo - Currency code to convert to
   * @return
   */
  public double getConvertedValue(double valueToConvert, String convertFrom, String convertTo)
  {
    try
    {
      this.valueToConvert = valueToConvert;
      this.convertFrom = convertFrom;
      this.convertTo = convertTo;
      String convertedValue = extractConvertedValue(convert());
      if (convertedValue != null && isNumeric(convertedValue))
      {
        BigDecimal roundVal = new BigDecimal(convertedValue);
        roundVal.round(new MathContext(2, RoundingMode.HALF_UP));
        return roundVal.doubleValue();
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace(System.out);
    }
    return 0d;
  }
 
  /**Connect to Google api using http GET request to perform the currency conversion**/
  private String convert()
  {
    try
    {
      String code = String.valueOf("/finance/converter?a=" + valueToConvert + "&from=" + convertFrom + "&to=" + convertTo);
      URL converterUrl = new URL("http://www.google.com" + code);
      URLConnection urlConnection = converterUrl.openConnection();
 
      InputStream inputStream = urlConnection.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      boolean stop = false;
      String result = "";
      
      while(!stop){
    	  String line = bufferedReader.readLine();
    	  if(line == null){
    		  stop = true;
    	  }else{
    		  if(line.contains("<span class=bld>")){
    			  int spanStart = line.indexOf("<span class=bld>", 0);
    			  int spanEnd = line.indexOf("</span>", 0);
    			  String subString = line.substring(spanStart, spanEnd);
    			  result = subString.replace("<span class=bld>", "");
    			  stop = true;
    		  }
    	  }
      }
      
      bufferedReader.close();
      inputStream.close();
      urlConnection = null;
 
      return result;
    }
    catch (Exception e)
    {
      e.printStackTrace(System.out);
    }
    return null;
  }
 
  /**If error is found within the response string, throw runtime exception to report, else parse the result for extraction**/
  private String extractConvertedValue(String convertedResult) throws Exception {
	  if(!convertedResult.isEmpty()){
		  String[] aux = convertedResult.split(" ");
		  if (isNumeric(aux[0])){
			  return aux[0];
		  }else
			  throw new RuntimeException("Error occured while converting amount: "+aux[0]);
	  }else{
		  throw new RuntimeException("Result from the web query is empty.");
	  }
  }
 
  private boolean isNumeric(String str)
  {
    return str.matches(regExp);
  }
 
}