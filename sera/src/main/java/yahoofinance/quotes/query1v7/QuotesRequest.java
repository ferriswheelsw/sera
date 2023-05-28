package yahoofinance.quotes.query1v7;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes2.CrumbManager;
import yahoofinance.util.RedirectableRequest;

public abstract class QuotesRequest<T> {
  private static final Logger log = LoggerFactory.getLogger(QuotesRequest.class);
  
  private static final ObjectMapper objectMapper = new ObjectMapper();
  
  protected final String symbols;
  
  public QuotesRequest(String symbols) {
    this.symbols = symbols;
  }
  
  public String getSymbols() {
    return this.symbols;
  }
  
  protected abstract T parseJson(JsonNode paramJsonNode);
  
  public T getSingleResult() throws IOException {
    List<T> results = getResult();
    if (results.size() > 0)
      return results.get(0); 
    return null;
  }
  
  public List<T> getResult() throws IOException {
    List<T> result = new ArrayList<>();
    Map<String, String> params = new LinkedHashMap<>();
    params.put("symbols", this.symbols);
    System.out.println("hi");
//    params.put("crumb", CrumbManager.getCrumb());
    System.out.println("bye");
//    String url = YahooFinance.QUOTES_QUERY1V7_BASE_URL + "?" + Utils.getURLParameters(params);
    String url = "https://query2.finance.yahoo.com/v7/finance/options/" + this.symbols;
    System.out.println(url);
    log.info("Sending request: " + url);
    URL request = new URL(url);
    RedirectableRequest redirectableRequest = new RedirectableRequest(request, 5);
    redirectableRequest.setConnectTimeout(YahooFinance.CONNECTION_TIMEOUT);
    redirectableRequest.setReadTimeout(YahooFinance.CONNECTION_TIMEOUT);
    URLConnection connection = redirectableRequest.openConnection();
    InputStreamReader is = new InputStreamReader(connection.getInputStream());

    // testing
    if (is.equals(null)){
      System.out.println("null");
    }else{
      System.out.println("ok");
    }


    try {
      JsonNode node = objectMapper.readTree(is);
      if (node.has("optionChain") && node.get("optionChain").has("result")) {
        //testing
        if (node.get("optionChain").get("result").get(0).has("quote")){
          System.out.println("haschain");
        }

        node = node.get("optionChain").get("result").get(0).get("quote");

        //testing
        if(node.equals(null)){
          System.out.println("null");
        }else{
          System.out.println(parseJson(node));
          System.out.println(node.asText());
        }


        for (int i = 0; i < node.size(); i++){
          System.out.print("hi ");
          System.out.println(i);
          result.add(parseJson(node));
          //System.out.println(parseJson(node.get(i)));
        }


      } else {
        throw new IOException("Invalid response");
      } 
      is.close();
    } catch (Throwable throwable) {
      try {
        is.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return result;
  }
}


/* Location:              C:\Users\sonja\Downloads\YahooFinanceAPI-3.16.4.jar!\yahoofinance\quotes\query1v7\QuotesRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */