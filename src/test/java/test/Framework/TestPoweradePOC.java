package test.Framework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;

public class TestPoweradePOC extends Common {
    @Parameters({"urlpowerade"})
    @Test
    public void LaunchSite(String urlpowerade) {
        help.launchUrl(urlpowerade);
        help.checkPageLoad();
        List<WebElement> allProduct = driver.findElements(By.xpath(prop.getLocator("PLPContainer")));
        if (allProduct != null)
        {
            for (int eachProduct = 0; eachProduct < allProduct.size(); eachProduct++) {

                List<WebElement> categoryForEachProduct = help.verifyProduct(eachProduct);
                int updatedCategorySize = 0;
                boolean isCategoryPresent = true;
                if (categoryForEachProduct.size() == 0) {
                    isCategoryPresent = false;
                    updatedCategorySize = updatedCategorySize + 1;
                } else {
                    updatedCategorySize = categoryForEachProduct.size();
                }
                for (int eachCategory = 0; eachCategory < updatedCategorySize; eachCategory++) {
                    LinkedHashMap<Integer,String> flavorForEachCategory = help.verifyCategoryForEachProduct2(eachCategory, isCategoryPresent);
                    String flavorName = "";
                    for (int eachFlavor = 0; eachFlavor < flavorForEachCategory.size(); eachFlavor++) {
                        try {
                            LinkedHashMap<Integer, String> sizesForEachFlavor = help.verifyFlavorForEachProductCategory2(eachFlavor, flavorName, flavorForEachCategory.get(eachFlavor));
                            for (int eachSize = 0; eachSize < sizesForEachFlavor.size(); eachSize++) {
                                help.verifySizesForEachFlavor2(eachSize,sizesForEachFlavor.get(eachSize));
                            }
                        } catch (Exception e) {
                            /**
                             * applied continue in case there is change in flavors list after switching values
                             * KNOWN BUG: dropdown values changes after changing the value
                             * Description: Flavors size drop down values gets changed after selecting second value inside it
                             * Coca-Cola Life >> coca-cola-zero-sugar/coca-cola-zero-sugar/
                             * coca-cola-zero-sugar/caffeine-free/
                             * */
                            continue;
                        }
                    }
                    help.closeTab();
                }
                if (isCategoryPresent) {
                    help.closeTab();
                }
            }
        }
    }
}