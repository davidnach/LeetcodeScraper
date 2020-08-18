package scrapers;

import Shared.SignIn;
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

public class SubmissionsScraper {

    static final String submissionsOutputPath = "output/submissions.txt";
    static final String signInUrl = "https://leetcode.com/accounts/login/";
    static final String chromeDriverPath = System.getProperty("user.dir") + "/lib/chromeDriver/chromeDriver";
    static final  String firefoxDriverPath = "/usr/local/bin//geckodriver";
    static final String credentialsPath = System.getProperty("user.dir").concat("/config/credentials.txt");
    static final int maxRefresh = 5;
    static SignIn signIn;

    public static void main(String[] args) throws Exception{

        initializeCredentials();
        WebDriver driver = new FirefoxDriver();
        signIn.signInToProfile(driver,signInUrl);
        navigateToSubmissionsPage(driver);
        readSubmissionsPages(driver);
    }


    public static void navigateToSubmissionsPage(WebDriver driver){

        try {
            WebElement dropDown = new WebDriverWait(driver, Duration.ofSeconds(10).toSeconds()).
                    until(ExpectedConditions.elementToBeClickable(By.xpath("//img[following-sibling::i]")));
            dropDown.click();
            WebElement submissionsButton = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).
                    until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"nav-user-app\"]/span/ul/div[2]/li[2]/div/div[3]/div/div[1]/i")));
            submissionsButton.click();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void readSubmissionsPages(WebDriver driver) throws Exception{
        FileWriter submissionsOutput = new FileWriter(submissionsOutputPath);

        boolean morePagesToRead = true;
        boolean firstTableRead = true;
        int pagesRead = 0;
        int timesRefreshed = 0;
        List<WebElement> columnNames = new ArrayList<WebElement>();
        while(morePagesToRead) {
            try {
                WebElement table = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(
                        ExpectedConditions.presenceOfElementLocated(By.tagName("table")));

                //table found
                timesRefreshed = 0;
                if (firstTableRead) {
                    columnNames = (List) table.findElements(By.tagName("th"));
                    firstTableRead = false;
                }

                List<WebElement> rows = table.findElements(By.xpath("//tbody/tr"));
                List<WebElement> cols = table.findElements(By.tagName("td"));
                if(rows.size() == 0 || cols.size() == 0){
                    System.err.println("not getting " + pagesRead + "(nd) table");
                    System.exit(1);
                }
                readTable(rows, cols, columnNames.size(), submissionsOutput);
                pagesRead++;
                WebElement nextPageButton = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(
                        ExpectedConditions.elementToBeClickable(By.xpath("//a[span[contains(text(),'Older')]]")));
                nextPageButton.click();

            } catch (Exception e) {
                if(e.getMessage().contains("table")) {
                    if (timesRefreshed < maxRefresh){
                        System.out.println(e.getLocalizedMessage());
                        driver.navigate().refresh();
                        timesRefreshed++;
                    } else {
                        System.err.println("Wasn't able to find table after " + maxRefresh + "refresh(es)");
                        driver.close();
                        System.exit(-1);
                    }
                } else if (e.getMessage().contains("waiting for element to be clickable")){
                    System.out.println("read all submissions!");
                    morePagesToRead = false;
                }
                e.printStackTrace();
            }

        }
        submissionsOutput.close();

    }

    public static void initializeCredentials() throws Exception {
        signIn = new SignIn(credentialsPath);
    }

    public static void setDriverProperties(){
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
    }

    public static void readTable(List<WebElement> rows, List<WebElement> cols, int numCols,FileWriter output) throws Exception{
        for(int i = 0; i < rows.size(); i++){
            for(int j = 0; j < numCols; j++){
                if(j != 0){
                    output.write('\t');
                }
                output.write(cols.get( (i * numCols) + j).getText());
            }
            output.write('\n');
        }
    }

}
