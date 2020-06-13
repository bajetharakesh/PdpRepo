package test.Framework;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class Common {
	
	ExtentReports extent;
	ExtentTest logger;
	WebDriver driver;
	WebDriverHelp help;
	ReadProperties prop;
	private static final String reportPath = "/test-output/Report.html";
	private static final String configPath = "\\extent-config.xml";

	@BeforeSuite
	public void beforeSuite() {
		System.out.println("Suite initiated");
//		extent = new ExtentReports (System.getProperty("user.dir") +"/test-output/Report.html", true);
//      extent.loadConfig(new File(System.getProperty("user.dir")+"\\extent-config.xml"));
	}
	@BeforeClass
	public void setup(){
		help = new WebDriverHelp();
		driver = help.getWebDriver();
		prop =  new ReadProperties();
		help.setProperties(prop);
		extent = help.initiateExtentReport(reportPath,configPath);

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
		help.terminateExtentReport(extent);
		System.out.println("tear down");
	}
	
	@AfterSuite
	public void endReport(){
	System.out.println("Suite ended");
    }

//TODO: implement tags for test scripts

}
