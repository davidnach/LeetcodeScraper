import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.security.Key;
import java.util.concurrent.TimeUnit;

public class LeetCodeLogin {
    public static void main(String[] args){
        String pw = "Frog$areugly10";
        String un = "davidnachson@gmail.com";
        String chromeDriverPath = System.getProperty("user.dir") + "/lib/chromeDriver/chromeDriver";
        String problemUrlBase = "https://leetcode.com/problems/";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://leetcode.com/accounts/login");
        driver.findElement(By.xpath("//input[contains(@id,\"id_login\")]")).sendKeys(un);
        driver.findElement(By.xpath("//input[contains(@id,\"id_password\")]")).sendKeys(pw);
        driver.findElement(By.xpath("//input[contains(@id,\"id_password\")]")).sendKeys(Keys.ENTER);
        //System.out.println(driver.getTitle());
        //driver.quit();
    }
}
