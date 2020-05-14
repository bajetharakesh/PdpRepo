package test.Framework;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TestLogin extends Common {
	public static LinkedHashMap<String, String> PDPlinks = new LinkedHashMap<String,String>();

	/*@Parameters({"emailId", "password"})
	@Test
	public void LoginTest(String emailId, String password) {

		help.launchUrl("https://google.com");
		help.enterValue(prop.getLocator("searchBar"),"apple");
		help.click(prop.getLocator("apple"));
		System.out.println("Successfully Logged In");
		System.out.println(emailId);
		System.out.println(password);

		help.endTestCase("pass");
		
	}
*/
	@Parameters({"url1"})
	@Test
	public void LaunchSite(String url1){
	help.launchUrl(url1);
		String pageLoadStatus = "";
		JavascriptExecutor js;
			do {
				js = (JavascriptExecutor)driver;
				pageLoadStatus = (String)js.executeScript("return document.readyState");
			} while ( !pageLoadStatus.equals("complete") );
		if (pageLoadStatus.equals("complete")) {
			help.endTestCase("pass");
		}
		else {
			help.endTestCase("Fail");
		}
		List<WebElement> Pdps = null;
		String product = "";
		Pdps = driver.findElements(By.xpath(prop.getLocator("Cokeimglink")));
		HashMap<String,String> temp = new HashMap<String,String>();
		for(WebElement e : Pdps) {
			temp.clear();
			String Imagelink = e.getAttribute("src");
			String product_name = Imagelink.substring(Imagelink.indexOf("Thumbnails_")+11,Imagelink.indexOf(".png"));
			product = product_name;
			if(!product_name.contains("Coca-Cola")){
				product_name = product_name.replace("-"," ");
			}
			else {
				product_name = product_name.replace("Coca-Cola", "");
			}
			String updatedpath= prop.getLocator("CokeExplorelink")+"//preceding::div[@role=\"region\" and @aria-label=\""+"Coca-Cola "+product_name+"\"]";
			temp.put(product,updatedpath);
			//LinkedHashMap<String,String> clonedData= (LinkedHashMap<String, String>) temp.clone();
			PDPlinks.put(product,temp.get(product));
		}
		help.endTestCase("pass");
	}

	@Test
	public HashMap<String,String> RetrivePdp(){
		List<WebElement> Pdps = null;
		Pdps = driver.findElements(By.xpath(prop.getLocator("Cokeimglink")));
		for(WebElement e : Pdps) {
			System.out.println(e.getText());
		}
		help.endTestCase("pass");
	return PDPlinks;
	}



}
