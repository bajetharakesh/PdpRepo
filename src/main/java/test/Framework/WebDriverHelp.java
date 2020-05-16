package test.Framework;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebDriverHelp {
    private WebDriver driver;
    private ExtentTest logger;
    private WebDriverWait wait;
    private ReadProperties prop;
    private String systemName;
    private String daystamp;
    private void LaunchDriver() {
        WebDriverManager.chromedriver().version("81.0.4044.138").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-browser-side-navigation");

        //options.addArguments("--headless");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 15);
        daystamp = new SimpleDateFormat("yyyy_MM_dd_kk_mm_ss").format(new Date());
    }

    public WebDriver getWebDriver() {
        //TODO: use enum to optimize code
        if (driver == null) {
            LaunchDriver();
        }
        return driver;
    }

    public void setLogger(ExtentTest logg) {
        logger = logg;
    }

    public void setProperties(ReadProperties properties) {
        prop = properties;
    }

    public void launchUrl(String url) {
        try {
            System.out.println("launch url");
            driver.get(url);
            log("pass", url + " is successfully launched");

            systemName = url.split("[.]")[1].replace("-","");

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public void click(String element) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(getLocator(element))));
            driver.findElement(By.xpath(getLocator(element))).click();
            //log("pass", "Clicked");


        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage().substring(0, 121));
        }
    }

    public WebElement getEachElement(String element, int i) {
        //TODO: implement other methods of finding element
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(getLocator(element))));
            //log("pass", "Found Element");
            return driver.findElements(By.xpath(getLocator(element))).get(i);

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage().substring(0, 121));
        }
        return null;
    }

    public void click(WebElement webElement) {
        try {
            wait.until(ExpectedConditions.visibilityOf(webElement));
            webElement.click();
            log("pass", "Clicked");

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public String getLocator(String locator) {
        String actualLocator = "";

        actualLocator = systemName + "." + locator;
        if (prop.getLocator(actualLocator) != null) {
            return prop.getLocator(actualLocator);
        } else if (prop.getLocator(locator) != null) {
            return prop.getLocator(locator);
        } else {
            return "locator not present in locator.properties";
        }
    }
    public void enterValue(String element, String value) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(element)));
            driver.findElement(By.xpath(element)).sendKeys(value);
            log("pass", value + " is successfully entered");

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public void endTestCase(String result) {
        if (result.equalsIgnoreCase("pass")) {
            log("pass", "The Test Case is successfully passed");
        } else if (result.equalsIgnoreCase("fail")) {
            log("fail", "The Test Case is failed");
        }
    }

    public void checkPageLoad() {
        try {
            String pageLoadStatus = null;
            JavascriptExecutor js;
            do {
                js = (JavascriptExecutor) driver;
                pageLoadStatus = (String) js.executeScript("return document.readyState");
            } while (!pageLoadStatus.equals("complete"));
            if (pageLoadStatus.equals("complete")) {
                //log("pass","Page loaded successfully");
            } else {
                log("fail", "Issue in page loading" + driver.getCurrentUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    private void log(String status, String msg) {
        if (status.equalsIgnoreCase("pass")) {
            logger.log(LogStatus.PASS, msg);
        } else if (status.equalsIgnoreCase("fail")) {
            logger.log(LogStatus.FAIL, msg);
            captureScreenShot();
        }
    }

    private void logTitle(String title) {
        logger.log(LogStatus.INFO, "HTML", title);
    }

    private void captureScreenShot() {
        //TODO: get current step name to image and take full page screenshot
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_kk_mm_ss").format(new Date());
            String desc = System.getProperty("user.dir")+"//test-output//Screenshot//"+daystamp+"//"+timeStamp+".jpg";
            File deFile = new File(desc);
            FileUtils.copyFile(source, deFile);
            System.out.println("Screenshot taken");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private void checkBrokenImage(String locator) {
        try {
            int responseCode = getResponseCode(locator);
            if (responseCode / 400 != 1) {
                log("pass", "image is displayed as expected");
            } else {
                log("fail", "image is broken or not present");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    private int getTabsCount() {
        return driver.getWindowHandles().size();
    }

    private void OpenInNewTab(WebElement locator) {
        String LinkOpeninNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
        locator.sendKeys(LinkOpeninNewTab);
        switchToTab(getTabsCount() - 1);
        checkPageLoad();
    }

    private boolean switchToTab(int iWindow) {
        boolean bFlag = false;
        int wait_timer = 3;

        while (wait_timer > 0 && !bFlag) {
            ArrayList<String> allTabs = new ArrayList(driver.getWindowHandles());
            if (allTabs.size() > iWindow) {
                driver.switchTo().window((String) allTabs.get(iWindow));
                bFlag = true;
                if (bFlag == true) {
                    break;
                    //	log("pass","tab switched successfully");
                }
            } else {
                try {
                    //TODO: Optimize this wait here
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                --wait_timer;
                System.out.println("Window not Found:" + iWindow);
            }
        }

        return bFlag;
    }

    public void closeTab() {
        try {
            ArrayList<String> allTabs = new ArrayList(driver.getWindowHandles());
            int iWindow = allTabs.size() - 1;
            if (allTabs.size() > iWindow) {
                driver.switchTo().window((String) allTabs.get(iWindow)).close();
                switchToTab(getTabsCount() - 1);
            } else {
                System.out.println("Window not Found:" + iWindow);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    private void checkPreviewURL() {
        try {
            checkPageLoad();
            if (driver.getCurrentUrl().contains("preview")) {
                log("fail", "redirected to preview url");
            } else {
                //log("pass", "redirected to prod url only");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    private int getResponseCode(String url) {
        int responseCode = 0;
        try {
            URL obj = null;
            obj = new URL(url);
            HttpURLConnection con = null;
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
        return responseCode;
    }

    public List<WebElement> FindList(String locator) {
        List<WebElement> elementList = null;
        elementList = driver.findElements(By.xpath(locator));
        return elementList;
    }

    public void ScrollTillElement(List<WebElement> element, int flavor) {
        //WebElement element = driver.findElement(By.xpath(locator));
//        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", element);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element.get(flavor) );

        // Actions actions = new Actions(driver);
        //actions.moveToElement(element);
        //actions.perform();
    }

    public void verifySizesForEachFlavor(int k) {
        String sizeValue = "";
        try {
            click("sizesDropdown");
            sizeValue = getEachElement("sizesAvailable", k).getAttribute("innerText");
            getEachElement("sizesAvailable", k).click();
            checkPageLoad();
            checkBrokenImage(driver.findElement(By.xpath(getLocator("ImagePDP"))).getAttribute("src"));
            checkPreviewURL();
            log("pass", "Verified Sizes, Images, Preview URL for Size " + sizeValue);
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public List<WebElement> verifyFlavorForEachProductCategory(int flavor, String flavorName) {
        List<WebElement> sizesForEachFlavor = null;
        try {
            click("flavorsDropdown");
            flavorName = getEachElement("flavorsAvailable", flavor).getAttribute("innerText");
           //ScrollTillElement(driver.findElements(By.xpath(prop.getLocator("flavorsAvailable"))), flavor);
            //((JavascriptExecutor)driver).executeScript("window.scrollBy(200,300");
            getEachElement("flavorsAvailable", flavor).click();
            checkPageLoad();
            checkBrokenImage(driver.findElement(By.xpath(getLocator("ImagePDP"))).getAttribute("src"));
            checkPreviewURL();
            sizesForEachFlavor = driver.findElements(By.xpath(getLocator("sizesAvailable")));
            log("pass", "Verified Sizes, Images, Preview URL for Size " + flavorName);

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", "Issue in the above PDP Page while changing " + flavorName + " flavor to next flavor");
        }
        return sizesForEachFlavor;
    }

    public List<WebElement> verifyFlavorForEachProductCategoryPeacetea(int flavor, String flavorName) {
        List<WebElement> sizesForEachFlavor = null;
        try {
            click("flavorsDropdown");
            flavorName = getEachElement("flavorsAvailable", flavor).getAttribute("innerText");
            //ScrollTillElement(driver.findElements(By.xpath(prop.getLocator("flavorsAvailable"))), flavor);
            ((JavascriptExecutor)driver).executeScript("window.scrollBy(200,300)");
            getEachElement("flavorsAvailable", flavor).click();
            checkPageLoad();
            checkBrokenImage(driver.findElement(By.xpath(getLocator("ImagePDP"))).getAttribute("src"));
            checkPreviewURL();
            sizesForEachFlavor = driver.findElements(By.xpath(getLocator("sizesAvailable")));
            log("pass", "Verified Sizes, Images, Preview URL for Size " + flavorName);

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", "Issue in the above PDP Page while changing " + flavorName + " flavor to next flavor");
        }
        return sizesForEachFlavor;
    }

    public List<WebElement> verifyCategoryForEachProduct(int category, boolean categoryPresent) {
        List<WebElement> flavorForEachCategory = null;
        String categoryName = "";
        try {
            if (categoryPresent) { //TODO: Category name locator need to be updated for Coke energy
                //categoryName = getEachElement("cokeExplore_Learn", category).getAttribute("innerText");
                categoryName = checkCategory(category);
            }
            checkPreviewURL();
            click("flavorsDropdown");
            flavorForEachCategory = driver.findElements(By.xpath(getLocator("flavorsAvailable")));
            click("flavorsDropdown");
            logTitle(categoryName);
            log("pass", "Verified Image, Preview URL for Category ");

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", "Error in category Page " + categoryName);
        }
        return flavorForEachCategory;
    }

    public String checkCategory( int category) {
        //TODO:Optimize locator for PLPContainerImage
        String categoryImageurl="";
        try{
            categoryImageurl = getEachElement("PLPContainerImage", category).getAttribute("src");
        }
        catch(Exception e){
            categoryImageurl = getEachElement("PLPContainerImageEnergy", category).getAttribute("src");
        }
        //categoryImageurl = getEachElement("PLPContainerImage", category).getAttribute("src");
        checkBrokenImage(categoryImageurl);
        String categoryName = "";
        if(categoryImageurl.contains("energy")){
            categoryName = getEachElement("PLPEnergyimg",category).getAttribute("prodname");
            OpenInNewTab(getEachElement("PLPEnergyimg",category));
        }
        else if(driver.getCurrentUrl().contains("coca-cola-local-flavors")){
            categoryName = getEachElement("PLPProductNameLocalTastes",category).getAttribute("innerText");
            OpenInNewTab(getEachElement("cokeExplore_Learn", category));
        }
        else {
            categoryName = getEachElement("PLPProductName", category).getAttribute("innerText");  //Changes made on 5/13 at 01:29 am
            OpenInNewTab(getEachElement("cokeExplore_Learn", category));
        }
        return retriveName(categoryName);
    }

    public List<WebElement> verifyProduct(int product) {
        List<WebElement> categoryForEachProduct = null;
        String productName = "";
        try {
            productName = getEachElement("PLPProductName", product).getAttribute("innerText");
            String productImageUrl = getEachElement("PLPContainerImage", product).getAttribute("src");
            checkBrokenImage(productImageUrl);
            OpenInNewTab(getEachElement("cokeExplore_Learn", product));
            productName = retriveName(productName);
            if(productName.contains("Coca-Cola Life")){
             categoryForEachProduct = driver.findElements(By.xpath(getLocator("apple")));
            }
            else {
                categoryForEachProduct = driver.findElements(By.xpath(getLocator("cokeExplore_Learn")));
            }
            logTitle(productName);
            log("pass", "Verified Image, No Preview URL redirection on product ");

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", "Error in Product " + productName);
        }
        return categoryForEachProduct;
    }

    public String retriveName(String name) {
        if ((name.equalsIgnoreCase("") || name.equalsIgnoreCase("learn more"))) {
            String[] urlArray = driver.getCurrentUrl().split("/");
            return urlArray[urlArray.length - 1];
        }
        return name;
    }
//TODO implement interface
// implement multithreading for parallel execution of different applications
}
