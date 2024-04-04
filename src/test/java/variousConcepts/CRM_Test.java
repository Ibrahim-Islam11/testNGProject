package variousConcepts;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class CRM_Test {

	WebDriver driver;
	ExtentReports extent;
	ExtentTest test;

	String browser;
	String url;
	String userName;
	String password;
	String dashboardValidationText = "Dashboard";
	String userAlertValidationText = "Please enter your user name";
	String passwordAlertValidationText = "Please enter your password";
	String customerPageValidationText = "New Customer";
	String fullName = "Gunther Lardson";
	String companyName = "Techfios";
	String email = "demo@techfios.com";
	String phoneNum = "123456";
	String countryName = "United States of America";

	// Elements:
	By USERNAME_FIELD = By.xpath("//input[@id = 'user_name']");
	By PASSWORD_FIELD = By.xpath("//input[@id = 'password']");
	By SIGNIN_BUTTON_FIELD = By.xpath("//button[@id = 'login_submit']");
	By DASHBOARD_VALIDATION_FIELD = By.xpath("//strong[text() = 'Dashboard']");
	By CUSTOMER_MENU_FIELD = By.xpath("//span[text() = 'Customers']");
	By ADD_CUSTOMER_MENU_FIELD = By.xpath("//span[text() = 'Add Customer']");
	By ADD_CUSTOMER_PAGE_VALIDATION_FIELD = By.xpath("//strong[text() = 'New Customer']");
	By FULLNAME_FIELD = By.xpath("//input[@class = 'form-control name ']");
	By COMPANY_DROPDOWN_FIELD = By.xpath("//select[@name = 'company_name']");
	By EMAIL_FIELD = By.xpath("//input[@type = 'text' and @class = 'form-control email ']");
	By PHONE_NUMBER_FIELD = By.xpath("//input[@type = 'text' and @class = 'form-control phone ']");
	By COUNTRY_DROPDOWN_FIELD = By.xpath("//select[@name = 'country']");

	@BeforeMethod
	public void init() {

		if (browser.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
			driver = new ChromeDriver();
		} else if (browser.equalsIgnoreCase("edge")) {
			System.setProperty("webdriver.edge.driver", "drivers\\msedgedriver.exe");
			driver = new EdgeDriver();
		}

		// System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
		// driver = new ChromeDriver();

		// System.setProperty("webdriver.edge.driver", "drivers\\msedgedriver.exe");
		// driver = new EdgeDriver();

		driver.manage().deleteAllCookies();
		driver.get(url);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	}

	@Test(priority = 1)
	public void testLogin() {

		driver.findElement(USERNAME_FIELD).sendKeys(userName);
		driver.findElement(PASSWORD_FIELD).sendKeys(password);
		driver.findElement(SIGNIN_BUTTON_FIELD).click();

		// Validate that we are on the right page:
		Assert.assertEquals(driver.findElement(DASHBOARD_VALIDATION_FIELD).getText(), dashboardValidationText,
				"Dashboard page not found");

	}

	@AfterMethod
	public void tearDown() {

		driver.close();
		driver.quit();
	}

	@BeforeClass
	public void readConfig() {
		// InputStream, BufferReader, FileReader, Scanner

		try {
			InputStream input = new FileInputStream("src\\main\\java\\config\\config.properties");
			Properties prop = new Properties();
			prop.load(input);
			browser = prop.getProperty("browser");
			url = prop.getProperty("url");
			userName = prop.getProperty("userName");
			password = prop.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(priority = 2)
	public void testAlert() {

		driver.findElement(SIGNIN_BUTTON_FIELD).click();
		Assert.assertEquals(driver.switchTo().alert().getText(), userAlertValidationText, "Alert is not available");
		driver.switchTo().alert().accept();

		driver.findElement(USERNAME_FIELD).sendKeys(userName);
		driver.findElement(SIGNIN_BUTTON_FIELD).click();
		Assert.assertEquals(driver.switchTo().alert().getText(), passwordAlertValidationText, "Alert is not available");
		driver.switchTo().alert().accept();

	}

	@Test(priority = 3)
	public void testAddCustomer() {

		// Execute the testLogin method to land on the Dashboard page:
		testLogin();

		driver.findElement(CUSTOMER_MENU_FIELD).click();
		driver.findElement(ADD_CUSTOMER_MENU_FIELD).click();
		Assert.assertEquals(driver.findElement(ADD_CUSTOMER_PAGE_VALIDATION_FIELD).getText(),
				customerPageValidationText, "New Customer Page Not Found");

		driver.findElement(FULLNAME_FIELD).sendKeys(fullName + generateRandomNum(999));

//		Select sel = new Select(driver.findElement(COMPANY_DROPDOWN_FIELD));
//		sel.selectByValue(companyName);

		selectFromDropDown(COMPANY_DROPDOWN_FIELD, companyName);

		driver.findElement(EMAIL_FIELD).sendKeys(generateRandomNum(99999) + email);
		driver.findElement(PHONE_NUMBER_FIELD).sendKeys(phoneNum + generateRandomNum(9999));

		selectFromDropDown(COUNTRY_DROPDOWN_FIELD, countryName);

	}

	public int generateRandomNum(int bound) {

		Random random = new Random();
		return random.nextInt(bound);

	}

	public void selectFromDropDown(By element, String value) {
		Select sel = new Select(driver.findElement(element));
		sel.selectByValue(value);

	}
	
	@BeforeClass
	public void reportGenerator() {

		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extentReport.html");

		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		test = extent.createTest("Parallel Execution Test", "Description");
	}
	
	@AfterClass
	public void reporterClose() {
		extent.flush();
	}

}
