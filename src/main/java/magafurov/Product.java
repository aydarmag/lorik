package magafurov;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Product {
    int current = 1;
    int last = 0;
    WebPage productPage;

    {
        try {
            productPage = new WebPage();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void checkPageProduct(String urlToCheck) throws Throwable{
        this.productPage.seleniumDriver.get(urlToCheck);
        System.out.println(urlToCheck);
        Thread.sleep(15000);
        Map<String, String> map = getProductList();
        List<String> urls = new ArrayList(map.keySet());
        List<String> pagers = new ArrayList(map.values());
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            int pagerCounter = Integer.parseInt(pagers.get(i));
            Review review = new Review(1,pagerCounter/50 + 1);
            review.checkPage(url);
        }
        try {
            if (!(current==last)) {
                last = Integer.parseInt(productPage.seleniumDriver.findElement(By.xpath("//li[contains(@class,'pager-last')]")).getText());
            }
        } catch (Exception ex) {
            System.out.println(this.productPage.seleniumDriver.getPageSource());
            last = 1;
        }
        String newUrl;
        if (!(current == last)) {
            if (current > 1) {
                newUrl = urlToCheck.substring(0,urlToCheck.indexOf("="))
                        + "=" + current + "&" + urlToCheck.substring(urlToCheck.lastIndexOf("&") +1);;
                ++current;
                checkPageProduct(newUrl);
            } else {
                newUrl = urlToCheck.substring(0,urlToCheck.lastIndexOf("?"))
                        + "?page=" + current + "&" + urlToCheck.substring(urlToCheck.lastIndexOf("?") +1);
                ++current;
                checkPageProduct(newUrl);
            }
        }
    }

    private Map<String, String> getProductList() {
        WebElement table = productPage.seleniumDriver.findElementByClassName("srch-result-nodes");
        Map<String, String> allRows = new HashMap();
        for (WebElement row : table.findElements(By.tagName("li"))) {
            try {
                WebElement rat =  row.findElement(By.className("rating"));
                allRows.put(rat.findElement(By.xpath("./a")).getAttribute("href")
                        ,rat.findElement(By.xpath("./a/span[2]")).getText());
            } catch (Exception e) {
                System.out.println("Number of reviews has not been found");
            }
        }
        return allRows;
    }
}
