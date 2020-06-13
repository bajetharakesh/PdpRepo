package test.Framework;

import com.relevantcodes.extentreports.ExtentReports;
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
import java.util.LinkedHashMap;
import java.util.List;

public class WebDriverHelp {
    private WebDriver driver;
    private WebDriverWait wait;
    private ReadProperties prop;
    private  String dayStamp;
    private  ExtentTest logger;

    private void LaunchDriver() {
        try {
            WebDriverManager.chromedriver().version("81.0.4044.138").setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("start-maximized");
            options.addArguments("enable-automation");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-infobars");
            options.addArguments("--disable-browser-side-navigation");
            //URL url =  new URL("http://localhost:4444/wd/hub");
            //driver = new RemoteWebDriver(url,options);
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, 15);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public WebDriver getWebDriver() {
        //TODO: use enum to optimize code
        if (driver == null) {
            LaunchDriver();
        }
        return driver;
    }


    public void setProperties(ReadProperties properties) {
        prop = properties;
    }

    public void launchUrl(String url) {
        try {
            System.out.println("launch url");
            driver.get(url);
            log("pass", url + " is successfully launched");

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public void click(String element) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getLocator(element))));
            driver.findElement(By.xpath(prop.getLocator(element))).click();
            //log("pass", "Clicked");


        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage().substring(0, 121));
        }
    }

    public WebElement getEachElement(String element, int i) {
        //TODO: implement other methods of finding element
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(prop.getLocator(element))));
            //log("pass", "Found Element");
            return driver.findElements(By.xpath(prop.getLocator(element))).get(i);

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
                driver.switchTo().window(allTabs.get(iWindow));
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
                driver.switchTo().window(allTabs.get(iWindow)).close();
                switchToTab(getTabsCount() - 1);
            } else {
                System.out.println("Window not Found:" + iWindow);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public List<WebElement> FindList(String locator) {
        List<WebElement> elementList = null;
        elementList = driver.findElements(By.xpath(locator));
        return elementList;
    }

    public void ScrollTillElement(List<WebElement> element, int flavor) {
        //WebElement element = driver.findElement(By.xpath(locator));
//        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element.get(flavor));

        // Actions actions = new Actions(driver);
        //actions.moveToElement(element);
        //actions.perform();
    }

//TODO implement interface
// implement multithreading for parallel execution of different applications

    private void checkBrokenImage(String locator) {
        try {
            int responseCode = getResponseCode(locator);
            if (responseCode / 400 != 1) {
                log("pass", "image is displayed as expected");
            } else {
                String url = driver.getCurrentUrl();
                log("fail", "image is broken or not present page URL is " + url);
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

    public void verifySizesForEachFlavorGrp2(int k) {
        String sizeValue = "";
        try {
            click("sizesDropdown");
            sizeValue = getEachElement("sizesAvailable", k).getAttribute("innerText");
            getEachElement("sizesAvailable", k).click();
            checkPageLoad();
            //checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            try {
                checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            } catch (Exception e) {
                String url = driver.getCurrentUrl();
                log("fail", "Image is not displayed for " + sizeValue + " URL is " + url);
                captureScreenShot();
            }
            checkPreviewURL();
            log("pass", "Verified Sizes, Images, Preview URL for Size " + sizeValue);
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public void verifySizesForEachFlavor(int k, String sizeName) {
        String sizeValue = "";
        try {
            driver.navigate().refresh();
            checkPageLoad();
            click("sizesDropdown");
            //sizeValue = getEachElement("sizesAvailable", k).getAttribute("innerText");
            //getEachElement("sizesAvailable", k).click();
            //driver.findElement(By.xpath(prop.getLocator("sizesAvailable") + "[contains(.,'" + sizeName + "')]")).click();
            if (sizeName.contains("PACK")) {
                sizeName = sizeName.replace("ACK", "ack");
                sizeName = sizeName.replace("FL OZ", "fl oz");
            } else if (sizeName.contains("FL OZ")) {
                sizeName = sizeName.replace("FL OZ", "fl oz");
            } else if (sizeName.contains("bottle")) {
                sizeName = sizeName.replace("bottle", "Bottle");
            }
            driver.findElement(By.xpath("//ul/li[contains(.,'" + sizeName + "')]")).click();
            checkPageLoad();
            try {
                checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            } catch (Exception e) {
                String url = driver.getCurrentUrl();
                log("fail", "Image is not displayed for " + sizeName + " URL is " + url);
                captureScreenShot();
            }
            checkPreviewURL();
            log("pass", "Verified Sizes, Images, Preview URL for Size " + sizeName);
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
    }

    public List<WebElement> verifyFlavorForEachProductCategoryGrp2(int flavor, String flavorName) {
        List<WebElement> sizesForEachFlavor = null;
        String LatestFlavorName = "";
        try {
            click("flavorsDropdown");
            flavorName = getEachElement("flavorsAvailable", flavor).getAttribute("innerText");
            //ScrollTillElement(driver.findElements(By.xpath(prop.getLocator("flavorsAvailable"))), flavor);
            //((JavascriptExecutor)driver).executeScript("window.scrollBy(200,300");
            getEachElement("flavorsAvailable", flavor).click();
            checkPageLoad();
            //checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            try {
                checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            } catch (Exception e) {
                String url = driver.getCurrentUrl();
                log("fail", "Image is not displayed for " + flavorName + " URL is " + url);
                captureScreenShot();
            }
            checkPreviewURL();
            sizesForEachFlavor = driver.findElements(By.xpath(prop.getLocator("sizesAvailable")));
            log("pass", "Verified Sizes, Images, Preview URL for Flavor " + flavorName);
        } catch (Exception e) {
            e.printStackTrace();
            String[] urlArray1 = driver.getCurrentUrl().split("/");
            LatestFlavorName = urlArray1[urlArray1.length - 1];
            log("fail", "Issue in the above PDP Page while changing " + LatestFlavorName + " flavor to next flavor values inside it are getting changed");
        }
        return sizesForEachFlavor;
    }

    public LinkedHashMap<Integer, String> verifyFlavorForEachProductCategory(int flavor, String flavorName, String FlavorName) {
        List<WebElement> sizesForEachFlavor = null;
        LinkedHashMap<Integer, String> sizesForEachFlavor2 = new LinkedHashMap<Integer, String>();
        sizesForEachFlavor2.clear();
        String LatestFlavorName = "";
        try {
            click("flavorsDropdown");
            //flavorName = getEachElement("flavorsAvailable", flavor).getAttribute("innerText");
            //ScrollTillElement(driver.findElements(By.xpath(prop.getLocator("flavorsAvailable"))), flavor);
            //((JavascriptExecutor)driver).executeScript("window.scrollBy(200,300");

            //getEachElement("flavorsAvailable", flavor).click();
            driver.findElement(By.xpath(prop.getLocator("flavorsAvailable") + "//*[contains(.,'" + FlavorName + "')]")).click();
            checkPageLoad();
            driver.navigate().refresh();
            checkPageLoad();
            try {
                checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            } catch (Exception e) {
                String url = driver.getCurrentUrl();
                log("fail", "Image is not displayed for " + FlavorName + " URL is " + url);
                captureScreenShot();
            }
            checkPreviewURL();
            click("sizesDropdown");
            sizesForEachFlavor = driver.findElements(By.xpath(prop.getLocator("sizesAvailable")));
            for (int l = 0; l < sizesForEachFlavor.size(); l++) {
                String DropsizeName = "";
                DropsizeName = sizesForEachFlavor.get(l).getAttribute("innerText").trim();
                sizesForEachFlavor2.put(l, DropsizeName);
            }
            click("sizesDropdown");
            log("pass", "Verified Sizes, Images, Preview URL for Flavor " + FlavorName);
        } catch (Exception e) {
            e.printStackTrace();
            String[] urlArray1 = driver.getCurrentUrl().split("/");
            LatestFlavorName = urlArray1[urlArray1.length - 1];
            log("fail", "Issue in the above PDP Page while changing " + LatestFlavorName + " flavor to next flavor values inside it are getting changed");
        }
        return sizesForEachFlavor2;
    }

    public List<WebElement> verifyFlavorForEachProductCategoryPeacetea(int flavor, String flavorName) {
        List<WebElement> sizesForEachFlavor = null;
        //LinkedHashMap<Integer, String> sizesForEachFlavor2 = new LinkedHashMap<Integer, String>();
        //sizesForEachFlavor2.clear();
        String LatestFlavorName = "";
        try {
            click("flavorsDropdown");
            flavorName = getEachElement("flavorsAvailable", flavor).getAttribute("innerText");
            //ScrollTillElement(driver.findElements(By.xpath(prop.getLocator("flavorsAvailable"))), flavor);
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(200,300)");

            getEachElement("flavorsAvailable", flavor).click();
            checkPageLoad();
            // ((JavascriptExecutor) driver).executeScript("window.scrollBy(100,200)");
            //checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            try {
                checkBrokenImage(driver.findElement(By.xpath(prop.getLocator("ImagePDP"))).getAttribute("src"));
            } catch (Exception e) {
                String url = driver.getCurrentUrl();
                log("fail", "Image is not displayed for " + flavorName + " URL is " + url);
                captureScreenShot();
            }
            checkPreviewURL();
            sizesForEachFlavor = driver.findElements(By.xpath(prop.getLocator("sizesAvailable")));
            log("pass", "Verified Sizes, Images, Preview URL for Flavor " + flavorName);

        } catch (Exception e) {
            e.printStackTrace();
            String[] urlArray1 = driver.getCurrentUrl().split("/");
            LatestFlavorName = urlArray1[urlArray1.length - 1];
            log("fail", "Issue in the above PDP Page while changing " + LatestFlavorName + " flavor to next flavor drop down values inside flavors is getting changed");
        }
        return sizesForEachFlavor;
    }

    public List<WebElement> verifyCategoryForEachProductGrp2(int category, boolean categoryPresent) {
        List<WebElement> flavorForEachCategory = null;
        String categoryName = "";
        //((JavascriptExecutor) driver).executeScript("window.scrollBy(600,900)");
        try {
            if (categoryPresent) { //TODO: Category name locator need to be updated for Coke energy
                //categoryName = getEachElement("cokeExplore_Learn", category).getAttribute("innerText");
                categoryName = checkCategory(category);
            }
            checkPreviewURL();
            click("flavorsDropdown");
            flavorForEachCategory = driver.findElements(By.xpath(prop.getLocator("flavorsAvailable")));
            click("flavorsDropdown");
            if (!categoryName.equals("")) {
                logTitle(categoryName);
                log("pass", "Verified Image, Preview URL for Category ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", "Error in category Page " + categoryName);
        }
        return flavorForEachCategory;
    }

    public LinkedHashMap<Integer, String> verifyCategoryForEachProduct(int category, boolean categoryPresent) {
        List<WebElement> temp = null;
        LinkedHashMap<Integer, String> flavorForEachCategory = new LinkedHashMap<Integer, String>();
        flavorForEachCategory.clear();
        String categoryName = "";
        try {
            if (categoryPresent) { //TODO: Category name locator need to be updated for Coke energy
                //categoryName = getEachElement("cokeExplore_Learn", category).getAttribute("innerText");
                categoryName = checkCategory(category);
            }
            checkPreviewURL();
            click("flavorsDropdown");
            temp = driver.findElements(By.xpath(prop.getLocator("flavorsAvailable")));
            for (int k = 0; k < temp.size(); k++) {
                String DropFlavorName = "";
                DropFlavorName = temp.get(k).getAttribute("innerText").trim();
                flavorForEachCategory.put(k, DropFlavorName);
            }
            click("flavorsDropdown");
            if (!categoryName.equals("")) {
                logTitle(categoryName);
                log("pass", "Verified Image, Preview URL for Category ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", "Error in category Page " + categoryName);
        }

        return flavorForEachCategory;
    }

    public String checkCategory(int category) {
        //TODO:Optimize locator for PLPContainerImage
        String categoryImageurl = "";
        try {
            if (driver.getCurrentUrl().contains("coca-cola-energy")) {
                categoryImageurl = getEachElement("PLPContainerImageEnergy", category).getAttribute("src");
            } else {
                categoryImageurl = getEachElement("PLPContainerImage", category).getAttribute("src");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // categoryImageurl = getEachElement("PLPContainerImageEnergy", category).getAttribute("src");

        //categoryImageurl = getEachElement("PLPContainerImage", category).getAttribute("src");
        checkBrokenImage(categoryImageurl);
        String categoryName = "";
        if ((categoryImageurl.contains("energy")) && (driver.getCurrentUrl().contains("coca-cola-energy"))) { //changed here
            categoryName = getEachElement("PLPEnergyimg", category).getAttribute("prodname");
            OpenInNewTab(getEachElement("PLPEnergyimg", category));
            checkPageLoad();
        } else if (driver.getCurrentUrl().contains("coca-cola-local-flavors")) {
            categoryName = getEachElement("PLPProductNameLocalTastes", category).getAttribute("innerText");
            OpenInNewTab(getEachElement("cokeExplore_Learn", category));
            checkPageLoad();
        } else if (driver.getCurrentUrl().contains("powerade")) {
            categoryName = getEachElement("Powerade.cokeExplore_Learn", category).getAttribute("innerText");
            OpenInNewTab(getEachElement("Powerade.Explore_Learn", category));
            checkPageLoad();
        } else if (driver.getCurrentUrl().contains("vitamin")) {
            categoryName = getEachElement("VitaWater.Explore_Learn", category).getAttribute("prodname");
            OpenInNewTab(getEachElement("VitaWater.Explore_Learn", category));
            checkPageLoad();
        } else {
            categoryName = getEachElement("PLPProductName", category).getAttribute("innerText");  //Changes made on 5/13 at 01:29 am
            OpenInNewTab(getEachElement("cokeExplore_Learn", category));
            checkPageLoad();
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
            if (productName.contains("Coca-Cola Life")) {
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("apple")));
            } else if (productName.contains("POWERADE")) {
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("Powerade.Explore_Learn")));
            } else if (productName.contains("vitamin")) {
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("VitaWater.Explore_Learn")));
            } else {
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("cokeExplore_Learn")));
            }
            logTitle(productName);
            log("pass", "Verified Image, Preview URL on product " + productName);

        } catch (Exception e) {
            e.printStackTrace();
            log("fail", "Error in Product " + productName);
        }
        return categoryForEachProduct;
    }

    public List<WebElement> verifyProduct2(int product) {
        List<WebElement> categoryForEachProduct = null;
        String productName = "";
        try {
            if (driver.getCurrentUrl().contains("dunkin")) {
                productName = getEachElement("Dunkin.PLPProductName", product).getAttribute("innerText");
                String productImageUrl = getEachElement("PLPContainerZico", product).getAttribute("src");
                checkBrokenImage(productImageUrl);
                OpenInNewTab(getEachElement("Dunkin.PLPProduct", product));
                productName = retriveName(productName);
                logTitle(productName);
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("Zico.PLPProductName")));
            } else if (driver.getCurrentUrl().contains("zico")) {
                productName = getEachElement("Zico.PLPProductName", product).getAttribute("innerText");
                String productImageUrl = getEachElement("PLPContainerZico", product).getAttribute("src");
                checkBrokenImage(productImageUrl);
                OpenInNewTab(getEachElement("Zico.PLPProductName", product));
                productName = retriveName(productName);
                logTitle(productName);
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("Zico.PLPProductName")));
            } else if (driver.getCurrentUrl().contains("barqs")) {
                productName = getEachElement("Barqs.PLPProductName", product).getAttribute("innerText");
                String productImageUrl = getEachElement("PLPContainerImage", product).getAttribute("src");
                checkBrokenImage(productImageUrl);
                OpenInNewTab(getEachElement("Barqs.Explore_learn", product));
                productName = retriveName(productName);
                logTitle(productName);
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("Zico.PLPProductName")));
            } else if (driver.getCurrentUrl().contains("melloyello.com")) {
                productName = getEachElement("MelloYello.PLPProductName", product).getAttribute("prodname");
                String productImageUrl = getEachElement("PLPContainerZico", product).getAttribute("src");
                checkBrokenImage(productImageUrl);
                OpenInNewTab(getEachElement("MelloYello.PLPProductName", product));
                productName = retriveName(productName);
                logTitle(productName);
                categoryForEachProduct = driver.findElements(By.xpath(prop.getLocator("Zico.PLPProductName")));
            }
            log("pass", "Verified Image, Preview URL on product " + productName);

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
    public ExtentReports initiateExtentReport(String reportPath, String configPath) {
        dayStamp = new SimpleDateFormat("yyyy_MM_dd_kk_mm_ss").format(new Date());
        ExtentReports extent = new ExtentReports(System.getProperty("user.dir") + reportPath, true);
        extent.loadConfig(new File(System.getProperty("user.dir") + configPath));
        return extent;
    }
    public void terminateExtentReport(ExtentReports extent) {
        extent.flush();
        extent.close();
    }
    public void setLogger(ExtentTest logg) {
        logger = logg;
    }
    public void endTestCase(String result) {
        if (result.equalsIgnoreCase("pass")) {
            log("pass", "The Test Case is successfully passed");
        } else if (result.equalsIgnoreCase("fail")) {
            log("fail", "The Test Case is failed");
        }
    }

    public void log(String status, String msg) {
        if (status.equalsIgnoreCase("pass")) {
            logger.log(LogStatus.PASS, msg);
        } else if (status.equalsIgnoreCase("fail")) {
            logger.log(LogStatus.FAIL, msg);
            captureScreenShot();
        }
    }

    public void logTitle(String title) {
        logger.log(LogStatus.INFO, "HTML", title);
    }

    public void captureScreenShot() {
        //TODO: get current step name to image and take full page screenshot
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_kk_mm_ss").format(new Date());
            String desc = System.getProperty("user.dir") + "//test-output//Screenshot//" + dayStamp + "//" + timeStamp + ".jpg";
            File deFile = new File(desc);
            FileUtils.copyFile(source, deFile);
            System.out.println("Screenshot taken");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
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
            System.out.println("GET Response Code :: " + responseCode + " Image URL " + url);
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            log("fail", e.getMessage());
        }
        return responseCode;
    }

}
