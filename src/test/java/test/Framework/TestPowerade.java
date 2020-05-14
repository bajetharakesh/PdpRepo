package test.Framework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;

public class TestPowerade extends Common {
    public static LinkedHashMap<String, String> PDPlinks = new LinkedHashMap<String, String>();

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
                    if (eachCategory == 5) {
                        System.out.println("Direct PDP");
                    }
                    List<WebElement> flavorForEachCategory = help.verifyCategoryForEachProduct(eachCategory, isCategoryPresent);
                    String flavorName = "";
                    for (int eachFlavor = 0; eachFlavor < flavorForEachCategory.size(); eachFlavor++) {
                        try {
                            if (eachProduct == 1 && eachCategory == 0 && eachFlavor == 2) {
                                System.out.println("failing here");
                            }
                            List<WebElement> sizesForEachFlavor = help.verifyFlavorForEachProductCategory(eachFlavor, flavorName);
                            for (int eachSize = 0; eachSize < sizesForEachFlavor.size(); eachSize++) {
                                help.verifySizesForEachFlavor(eachSize);
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                    help.closeTab();
                }
                help.closeTab();
            }
        }
    }
}
