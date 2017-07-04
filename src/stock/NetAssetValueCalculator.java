package stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NetAssetValueCalculator {
    private StockPriceFetcher theFetcher;
    private List<String> invalidTickerList = new ArrayList<>();
    private List<String> networkFailureList = new ArrayList<>();

    public NetAssetValueCalculator(StockPriceFetcher fetcher){
        theFetcher = fetcher;
    }

    public int calculateValueOfAStockInCents(int numOfShares, int stockPrice) {
        return numOfShares * stockPrice;
    }

    public int getStockPrice(String ticker)  { return theFetcher.getPrice(ticker); }

    public Map<String, Integer> setCurrentPrices(Map<String, Integer> queries){
        Map<String, Integer> currentPriceList = new HashMap<>();
        if(queries.size()!=0) {
            for (Map.Entry<String, Integer> entry : queries.entrySet()) {
                String key = entry.getKey();
                int currentPrice = 0;
                try {
                    currentPrice = getStockPrice(key);
                } catch (Exception e) {
                    if(e.getMessage().equals("Invalid ticker"))
                        invalidTickerList.add(key);
                    else if(e.getMessage().equals("Network failure"))
                        networkFailureList.add(key);
                }
                currentPriceList.put(key, currentPrice);
            }
        }
        return currentPriceList;
    }

    public Map<String, Integer> setTotalValues (Map<String, Integer> queries, Map<String, Integer> priceList){
        Map<String, Integer> totalValueList = new HashMap<>();
        if(queries.size()!=0) {
            for (Map.Entry<String, Integer> entry : queries.entrySet()) {
                String key = entry.getKey();
                int share = entry.getValue();
                int price = priceList.get(key);

                totalValueList.put(key, calculateValueOfAStockInCents(share, price));
            }
        }
        return totalValueList;
    }

    public int computeNetAssetValues(Map<String, Integer> totalValList){
        int totalNetAssetVal = 0;
        if(totalValList.size()!=0) {
            for (Map.Entry<String, Integer> entry : totalValList.entrySet())
                totalNetAssetVal += entry.getValue();
        }

        return totalNetAssetVal;
    }

    public List<String> getInvalidTickerList(){return invalidTickerList;}
    public List<String> getNetworkFailureList(){return networkFailureList;}
}
