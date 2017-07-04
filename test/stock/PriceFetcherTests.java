package stock;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.ws.WebServiceException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PriceFetcherTests {
    PriceFetcher priceFetcher;
    String ticker = "FB";
    String invalidTicker = "APPL";

    @Before
    public void setup(){
        priceFetcher = new PriceFetcher();
    }


    @Test
    public void getStockPriceForFB(){
        try{
            assertTrue( priceFetcher.getPrice(ticker)>0 );

        }catch (Exception e){ }
    }

    @Test
    public void getStockPriceForInvalidTicker(){
        try{
            priceFetcher.getPrice(invalidTicker);
            fail("Excepted exception for invalid ticker");
        }catch (Exception e){ assertEquals("Invalid ticker", e.getMessage()); }
    }

    @Test
    public void getStockPriceForNetworkError(){
       PriceFetcher mockFetcher = new PriceFetcher(){
         @Override
         protected String setUrl(String ticker){ return "http://abcd"; }
       };
        try{
            mockFetcher.getPrice(ticker);
            fail("Excepted exception for network");
        }catch (Exception e){ assertEquals("Network failure", e.getMessage());}
    }
}

