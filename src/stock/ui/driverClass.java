package stock.ui;

import stock.NetAssetValueCalculator;
import stock.PriceFetcher;
import stock.StockPriceFetcher;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;


public class driverClass {
    private String fileName = "user_stock_request.txt";
    private Map<String, Integer> userQueries = new HashMap<>();
    private Map<String, Integer> stockPrices = new HashMap<>();
    private Map<String, Integer> stockValues = new HashMap<>();
    int totalNAV;

    StockPriceFetcher driverFetcher = new PriceFetcher();
    NetAssetValueCalculator calculator = new NetAssetValueCalculator(driverFetcher);

    public driverClass(){
        importUserRequest();
    }

    public static void main(String[] args){
        driverClass driver = new driverClass();
        driver.queryProcess();
        driver.displayResults();
    }

    private void queryProcess(){
        stockPrices = calculator.setCurrentPrices(userQueries);
        stockValues = calculator.setTotalValues(userQueries, stockPrices);
        totalNAV = calculator.computeNetAssetValues(stockValues);
    }

    private void displayResults(){
        System.out.println("-------------------------------------");
        System.out.println("Net asset value of all stock shares");
        System.out.println("TICKER" + "  SHARE" + "  PRICE" + "  VALUE");
        for(Map.Entry<String, Integer> entry:userQueries.entrySet()) {
            String ticker = entry.getKey();
            int share = entry.getValue();
            double price = ((double)stockPrices.get(ticker))/100;
            double value = ((double)(int)stockValues.get(ticker))/100;
            System.out.print(ticker+"  "+ share + "  " + price + "  " + value);
            System.out.println();
        }
        System.out.println("Total Net Asset Value is: " + ((double)totalNAV)/100);

        if(calculator.getInvalidTickerList().size()!=0){
            List<String> invalidList = calculator.getInvalidTickerList();
            System.out.print("Invalid ticker symbol(s): ");
            for(int i = 0; i<invalidList.size(); i++)
                System.out.print(invalidList.get(i)+" ");
            System.out.println();
        }

        if(calculator.getNetworkFailureList().size()!=0){
            List<String> failureList = calculator.getNetworkFailureList();
            System.out.print("Queries failed due to network error: ");
            for(int i = 0; i<failureList.size(); i++)
                System.out.print(failureList.get(i)+" ");
            System.out.println();
        }
    }

    private void importUserRequest() {
        BufferedReader br = null;
        try{
            String line;
            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine())!=null){
                String tempTicker;
                int tempShare;
                String[] tokens = line.split("\\s+");
                tempTicker = tokens[0];
                tempShare = Integer.parseInt(tokens[1]);
                userQueries.put(tempTicker, tempShare);
            }
        }catch (IOException e){}finally {
            try{
                if(br!=null)
                    br.close();
            }catch (IOException ex){ex.printStackTrace();};
        }

    }
}

