package stock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.ws.WebServiceException;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class NetAssetValueCalculatorTests {
    NetAssetValueCalculator calculator;
    StockPriceFetcher fetcher;
    private final String ticker_GOOG = "GOOG";
    private final String ticker_FB = "FB";
    private Map<String, Integer> userQueries;
    private Map<String, Integer> stockPrices;
    private Map<String, Integer> stockValues;

    @Before
    public void setup(){
        //fetcher = Mockito.mock(StockPriceFetcher.class);
        fetcher = Mockito.mock(PriceFetcher.class);
        calculator = new NetAssetValueCalculator(fetcher);
        userQueries = new HashMap<>();
        stockPrices = new HashMap<>();
        stockValues = new HashMap<>();
    }

    @Test
    public void canary(){
        assertTrue(true);
    }

    @Test
    public void calculateValueOfAStockWhenShareIsZero(){

        assertEquals(0, calculator.calculateValueOfAStockInCents(0, 500));
    }

    @Test
    public void calculateValueOfAStockWhenShareIsOne(){
        assertEquals(523, calculator.calculateValueOfAStockInCents(1, 523));
    }

    @Test
    public void calculateValueOfAStock(){
        assertEquals(6, calculator.calculateValueOfAStockInCents(2, 3));
    }

    @Test
    public void getStockPriceOfGOOG() {
        when(fetcher.getPrice(ticker_GOOG)).thenReturn(2058);

        assertEquals(2058, calculator.getStockPrice(ticker_GOOG));
    }

    @Test
    public void getStockPriceReceivesExceptionWhenTickerIsInvalid() {
        when(fetcher.getPrice("invalid_ticker")).thenThrow(IllegalArgumentException.class);

        try{
            calculator.getStockPrice("invalid_ticker");
            fail("Excepted exception for invalid ticker");
        }catch (IllegalArgumentException e){  }
    }

    @Test
    public void getStockPriceReceivesExceptionWhenNetworkFails(){
        when(fetcher.getPrice(ticker_GOOG)).thenThrow(WebServiceException.class);
        try{
            calculator.getStockPrice(ticker_GOOG);
            fail("Excepted exception for network failure");
        }catch (WebServiceException e){ }
    }

    @Test
    public void setStockPriceForWhenOnlyOneTicker(){
        userQueries.put(ticker_FB, 8);
        //when(calculator.getStockPrice(ticker_FB)).thenReturn(5);
        when(fetcher.getPrice(ticker_FB)).thenReturn((5));
        stockPrices = calculator.setCurrentPrices(userQueries);

        assertEquals(5, (int)stockPrices.get(ticker_FB));
    }

    @Test
    public void setStockPricesWithTwoValidTickers(){
        userQueries.put(ticker_FB, 8);
        userQueries.put(ticker_GOOG, 10);
        //when(calculator.getStockPrice(ticker_FB)).thenReturn(5);
        //when(calculator.getStockPrice(ticker_GOOG)).thenReturn(10);
        when(fetcher.getPrice(ticker_FB)).thenReturn((5));
        when(fetcher.getPrice(ticker_GOOG)).thenReturn((10));
        stockPrices = calculator.setCurrentPrices(userQueries);

        assertEquals(5, (int)stockPrices.get(ticker_FB));
        assertEquals(10, (int)stockPrices.get(ticker_GOOG));
    }

    @Test
    public void setStocksPriceWhenThereIsOnlyOneTickerAndItIsInvalid() {
        userQueries.put("invalidTicker", 8);
        //when(calculator.getStockPrice("invalidTicker")).thenThrow(new RuntimeException("Invalid ticker"));
        when(fetcher.getPrice("invalidTicker")).thenThrow(new RuntimeException("Invalid ticker"));

        stockPrices = calculator.setCurrentPrices(userQueries);

        assertEquals("invalidTicker", calculator.getInvalidTickerList().get(0));
    }

    @Test
    public void setStockPricesWhenThereIsOnlyOneTickerAndNetworkFails() {
        userQueries.put(ticker_GOOG, 8);
//        when(calculator.getStockPrice(ticker_GOOG)).thenThrow(new RuntimeException("Network failure"));
        when(fetcher.getPrice(ticker_GOOG)).thenThrow(new RuntimeException("Network failure"));
        stockPrices = calculator.setCurrentPrices(userQueries);

        assertEquals(ticker_GOOG, calculator.getNetworkFailureList().get(0));
    }

    @Test
    public void setStockPricesWithOneValidTickerOneInvalidTicker() {
        userQueries.put(ticker_GOOG, 10);
        userQueries.put("invalidTicker", 8);

        when(calculator.getStockPrice("invalidTicker")).thenThrow(new RuntimeException("Invalid ticker"));
        when(calculator.getStockPrice(ticker_GOOG)).thenReturn(10);
        stockPrices = calculator.setCurrentPrices(userQueries);

        assertEquals(10,(int) stockPrices.get(ticker_GOOG));
        assertEquals(0, (int)stockPrices.get("invalidTicker"));
    }

    @Test
    public void getStockPricesForTwoTickersWithOneSuccessesAndOneFails() {
        userQueries.put("tickerWithNetworkError", 8);
        userQueries.put(ticker_GOOG, 10);
        when(calculator.getStockPrice("tickerWithNetworkError")).thenThrow(new RuntimeException("Network failure"));
        when(calculator.getStockPrice(ticker_GOOG)).thenReturn(10);
        stockPrices = calculator.setCurrentPrices(userQueries);

        assertEquals(0, (int)stockPrices.get("tickerWithNetworkError"));
        assertEquals(10, (int)stockPrices.get(ticker_GOOG));
    }


    @Test
    public void getTotalValueOfAllStocksWhenThereIsNoTicker() {
        stockPrices = calculator.setCurrentPrices(userQueries);
        stockValues = calculator.setTotalValues(userQueries, stockPrices);

        assertEquals(0, calculator.computeNetAssetValues(stockValues));
    }

    @Test
    public void getTotalValueOfOneStocksWhenThereIsOneTicker() {
        userQueries.put(ticker_GOOG, 8);
        when(calculator.getStockPrice(ticker_GOOG)).thenReturn(10);
        stockPrices = calculator.setCurrentPrices(userQueries);
        stockValues = calculator.setTotalValues(userQueries, stockPrices);

        assertEquals(80, calculator.computeNetAssetValues(stockValues));
    }

    @Test
    public void getTotalValOfAllStocksWhenThereAreTwoTickers() {
        userQueries.put(ticker_GOOG, 8);
        userQueries.put(ticker_FB, 20);
        when(calculator.getStockPrice(ticker_GOOG)).thenReturn(10);
        when(calculator.getStockPrice(ticker_FB)).thenReturn(20);
        stockPrices = calculator.setCurrentPrices(userQueries);
        stockValues = calculator.setTotalValues(userQueries, stockPrices);


        assertEquals(480, calculator.computeNetAssetValues(stockValues));
    }

}