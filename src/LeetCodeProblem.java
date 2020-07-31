import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LeetCodeProblem {

    static String password;
    static String userName;
    static String submissionsOutputPath = "output/submissions.txt";
    static String signInUrl = "https://leetcode.com/accounts/login/";
    static String chromeDriverPath = System.getProperty("user.dir") + "/lib/chromeDriver/chromeDriver";
    static String firefoxDriverPath = "/usr/local/bin//geckodriver";

    public static void main(String[] args) throws Exception{
        initialize();
        WebDriver driver = new FirefoxDriver();
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        signInToProfile(driver);
        navigateToSubmissionsPage(driver);
        readSubmissionsPages(driver);
        //driver.quit();
    }

    public static void signInToProfile(WebDriver driver){
        driver.get(signInUrl);
        WebElement loginElement = new WebDriverWait(driver, Duration.ofSeconds(10).toSeconds()).
                until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#id_login")));
        loginElement.sendKeys(userName);
        driver.findElement(By.cssSelector("#id_password")).sendKeys(password);
        driver.findElement(By.cssSelector("#id_password")).sendKeys(Keys.ENTER);
    }

    public static void navigateToSubmissionsPage(WebDriver driver){
        WebElement dropDown = new WebDriverWait(driver, Duration.ofSeconds(10).toSeconds()).
                until(ExpectedConditions.elementToBeClickable(By.xpath("//img[following-sibling::i]")));
        dropDown.click();
        WebElement submissionsButton = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).
                until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"nav-user-app\"]/span/ul/div[2]/li[2]/div/div[3]/div/div[1]/i")));
        submissionsButton.click();
    }

    public static void readSubmissionsPages(WebDriver driver) throws Exception{
        FileWriter submissionsOutput = new FileWriter(submissionsOutputPath);

        boolean morePagesToRead = true;
        boolean firstTableRead = true;
        int pagesRead = 0;
        List<WebElement> columnNames = new ArrayList<WebElement>();
        while(morePagesToRead) {
            try {
                WebElement table = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(
                        ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
                if (firstTableRead) {
                    columnNames = (List) table.findElements(By.tagName("th"));
                    firstTableRead = false;
                }
                List<WebElement> rows = table.findElements(By.xpath("//tbody/tr"));
                List<WebElement> cols = table.findElements(By.tagName("td"));
                if(rows.size() == 0 || cols.size() == 0){
                    System.out.println("not getting " + pagesRead + "(nd) table");
                    return;
                }
                readTable(rows, cols, columnNames.size(), submissionsOutput);
                pagesRead++;
                WebElement nextPageButton = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(
                        ExpectedConditions.elementToBeClickable(By.xpath("//a[span[contains(text(),'Older')]]")));
                nextPageButton.click();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                if(e.getMessage().contains("table")){
                    System.out.println(e.getLocalizedMessage());
                    driver.navigate().refresh();
                } else {
                    morePagesToRead = false;
                }
                System.out.println("catch block");
                e.printStackTrace();
            }


        }
        submissionsOutput.close();

    }


    public static void readTable(List<WebElement> rows, List<WebElement> cols, int numCols,FileWriter output) throws Exception{
        for(int i = 0; i < rows.size(); i++){
            for(int j = 0; j < numCols; j++){
                if(j != 0){
                   // System.out.print(" ");
                    output.write('\t');
                }
                //System.out.print(cols.get( (i * numCols) + j).getText());
                output.write(cols.get( (i * numCols) + j).getText());
            }
            output.write('\n');
        }
    }

    public static void initialize() throws Exception{
        //System.out.println(new File(System.getProperty("user.dir").concat("/config/credentials.txt")));
        Scanner reader = new Scanner(new File(System.getProperty("user.dir").concat("/config/credentials.txt")));
        int i = 0;
        while(reader.hasNextLine()){
            if(i == 0){
                userName = reader.nextLine();
            } else {
                password = reader.nextLine();
            }
            i++;
        }

        if(i != 2){
            System.err.println("file should include username and password on two seperate lines");
        }

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.gecko.driver",firefoxDriverPath);
    }
}
