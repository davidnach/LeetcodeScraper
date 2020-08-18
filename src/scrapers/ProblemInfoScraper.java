package scrapers;

import Shared.SignIn;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class ProblemInfoScraper {

    static final String problemUrlsPath = System.getProperty("user.dir").concat("/output/lcProblems.tsv");
    static final String firefoxDriverPath = "/usr/local/bin/geckodriver";
    static final String chromeDriverPath = System.getProperty("user.dir").concat("/lib/chromeDriver/chromeDriver");
    static final String userProblemsInfoPath = System.getProperty("user.dir").concat("/output/users/");
    static final String problemUrlBase = "https://leetcode.com/problems/";
    static final String credentialsPath = System.getProperty("user.dir").concat("/config/credentials.txt");
    static final String relatedTopicsXpath = "//div[text() = 'Related Topics']";
    static final String relatedTopicElementsXpath = "//div[@class = 'css-vrmejz']/a/span";
    static final String similarQuestionsXpath = "//div[text() = 'Similar Questions']";
    static final String similarQuestionElementsXpath = "//div[@class = 'css-g5i00m']/div/a";
    static boolean skipPremium = true;
    static SignIn signIn;
    static String userName;

    public static void main(String[] args) throws Exception{

        initializeCredentials();
        createOutputFolder();
        setDriverProperties();
        FileWriter output = new FileWriter(userProblemsInfoPath.concat("/" + userName + ".txt"));
        WebDriver driver = new FirefoxDriver();

        List<String> problemUrls = getProblemUrls(problemUrlBase,10);


        for (String url : problemUrls) {
            driver.get(url);
             List<WebElement> foundRelatedTopics = ProblemInfoScraper.scrapeDropDownElements(driver, relatedTopicsXpath, relatedTopicElementsXpath);
            List<WebElement> foundSimilarQuestions = ProblemInfoScraper.scrapeDropDownElements(driver, similarQuestionsXpath, similarQuestionElementsXpath);
            try {
                ProblemInfoScraper.writeProblemInfo(output, foundRelatedTopics, foundSimilarQuestions, url, '\t', ',');
            } catch(Exception e){
                e.printStackTrace();
            }
            output.write('\n');
        }
        driver.quit();
        output.close();

    }

    public static List<WebElement> scrapeDropDownElements(WebDriver driver, String xpathDropDown, String xpathDropDownElements){

        List<WebElement> dropDownElements = new ArrayList<WebElement>();
        try {
            WebElement dropDownButton = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(
                        ExpectedConditions.elementToBeClickable(By.xpath(xpathDropDown)));
            dropDownButton.click();
            WebElement dropDownElement = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathDropDownElements)));
            TimeUnit.SECONDS.sleep(1);
            dropDownElements = driver.findElements(By.xpath(xpathDropDownElements));
        } catch (Exception e) {
            e.printStackTrace();
            return dropDownElements;
        }
        return dropDownElements;
    }

    public static void initializeCredentials() throws Exception {
        signIn = new SignIn(credentialsPath);
        userName = signIn.getUserName();
    }

    public static void createOutputFolder() throws Exception{
        if(!Files.exists(Paths.get(userProblemsInfoPath))) {
            Files.createDirectory(Paths.get(userProblemsInfoPath));
        }
    }
    public static void printTextOfElements(List<WebElement> elements){
        for(WebElement element : elements){
            System.out.println(element.getText());
        }
    }

    public static void setDriverProperties(){
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
    }

    public static List<String> getProblemUrlTitles() throws Exception{
        List<String> titles = new ArrayList<String>();
        Scanner sc = new Scanner(new File(problemUrlsPath));

        while(sc.hasNextLine()){
            String[] tokens = sc.nextLine().split("\t");
            if(!isPremium(tokens[1])){
                titles.add(tokens[0]);

            }
        }
        sc.close();
        return titles;
    }

    public static List<String> getProblemUrls(String problemUrlBase, int limit) throws Exception{
        List<String> suffices = new ArrayList<String>();
        Scanner sc = new Scanner(new File(problemUrlsPath));
        int added = 0;
        while(sc.hasNextLine() && added < limit){
            String[] tokens = sc.nextLine().split("\t");
            if(!isPremium(tokens[3])){
                suffices.add(problemUrlBase.concat(tokens[0]));
                added++;
            }
        }
        sc.close();
        return suffices;
    }

    public static WebElement tryGetElement(WebDriver driver, String xpathDropDown){

        WebElement dropDownButton;
        try {
            dropDownButton = new WebDriverWait(driver, Duration.ofSeconds(5).toSeconds()).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(xpathDropDown)));
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return dropDownButton;
    }

    public static boolean isPremium(String isPremium){
        return isPremium.equals("true");
    }

    public static void writeProblemInfo(FileWriter writer,
                                        List<WebElement> relatedTopics,
                                        List<WebElement> similarQuestions,
                                        String problemTitle,
                                        Character fieldDelimiter,
                                        Character inFieldDelimiter) throws Exception
    {
        writer.write(problemTitle);
        writer.write(fieldDelimiter);
        boolean first = true;
        for(WebElement element : relatedTopics){
            if(first){
                first = false;
            } else {
                writer.write(inFieldDelimiter);
            }
            writer.write(element.getText());
        }

        first = true;
        writer.write(fieldDelimiter);
        for(WebElement element : similarQuestions){
            if(first){
                first = false;
            } else {
                writer.write(inFieldDelimiter);
            }
            writer.write(element.getText());
        }
    }
}
