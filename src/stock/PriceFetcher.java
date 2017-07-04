package stock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PriceFetcher implements StockPriceFetcher{
    String partialUrl = "http://ichart.finance.yahoo.com/table.csv?s=";
    int currentStockPrice = 0;

    public  int getPrice(String ticker) {
        String url = setUrl(ticker);
        try{
            URL yahooFinance = new URL(url);
            URLConnection data = yahooFinance.openConnection();

            Scanner input = new Scanner(data.getInputStream());

            if (input.hasNext())
                input.nextLine();
            String line = input.nextLine();
            currentStockPrice = (int) (parser(line) * 100);

        }catch (IOException e){
            currentStockPrice = 0;
            System.out.println("exception is:" + e.getClass().getName());
            if(e instanceof FileNotFoundException)
            throw new RuntimeException("Invalid ticker");
            else if(e instanceof UnknownHostException)
                throw new RuntimeException("Network failure");
        }catch (Exception e){
            currentStockPrice = 0;
        }

        return currentStockPrice;
    }
    protected String setUrl(String ticker){
        return partialUrl+ticker;
    }

    private double parser(String str){
        String delims = ",";
        String[] tokens = str.split(delims);
        return Double.parseDouble(tokens[tokens.length-1]);
    }


}
