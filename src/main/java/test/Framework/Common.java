package test.Framework;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;

public class Common {
	
	ExtentReports extent;
	ExtentTest logger;
	WebDriver driver;
	WebDriverHelp help;
	ReadProperties prop;
	

	@BeforeSuite
	public void beforeSuite() {
		
		extent = new ExtentReports (System.getProperty("user.dir") +"/test-output/Report.html", true);
        extent.loadConfig(new File(System.getProperty("user.dir")+"\\extent-config.xml"));
	}
	@BeforeClass
	public void setup(){
		help = new WebDriverHelp(); 
		driver = help.getWebDriver();
		prop =  new ReadProperties();
		help.setProperties(prop);
	}
	
	@BeforeMethod
	public void intiateTest(){
		logger = extent.startTest(this.getClass().getSimpleName());
		help.setLogger(logger);
		
	}
	
	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			logger.log(LogStatus.FAIL, "Test Case Failed is "+result.getName());
			logger.log(LogStatus.FAIL, "Test Case Failed is "+result.getThrowable());
		}else if(result.getStatus() == ITestResult.SKIP){
			logger.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
		}
		extent.endTest(logger);
	}
	
	@AfterClass
	public void tearDown() {
		driver.quit();
		System.out.println("tear down");
	}
	
	@AfterSuite
	public void endReport(){
		extent.flush();
        extent.close();
    }

//	@DataProvider(name="urlList")
//	public static Object[][] getDataFromDataprovider(){
//		return new Object[][] {
//				{"https://us.coca-cola.com/products/"},
//				{"https://www.powerade.com/products/"},{"https://www.fresca.com/products"},
//				{"https://www.peacetea.com/products/"},{"https://www.vitaminwater.com/products/vitaminwater/"},
//				{"https://www.vitaminwater.com/products/vitaminwater-zero/"},{"https://www.drinkaha.com/products/"},
//				{"https://www.zico.com/products/"}
//		};
//	}
	@DataProvider(name="urlList")
	public static Object[][] getDataFromDataprovider(){
		return new Object[][] {
				{"https://www.drinkaha.com/products/"}
		};
	}

//TODO: implement tags for test scripts

}
