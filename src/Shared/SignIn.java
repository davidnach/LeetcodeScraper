package Shared;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Scanner;

public class SignIn {

    private String userName;
    private String password;

    public SignIn(String credentialsPath) throws Exception{
        initialize(credentialsPath);
    }


    public void signInToProfile(WebDriver driver, String signInUrl){
        try {
            driver.get(signInUrl);
            WebElement loginElement = new WebDriverWait(driver, Duration.ofSeconds(10).toSeconds()).
                    until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#id_login")));
            loginElement.sendKeys(userName);
            driver.findElement(By.cssSelector("#id_password")).sendKeys(password);
            driver.findElement(By.cssSelector("#id_password")).sendKeys(Keys.ENTER);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean signInToProfile(WebDriver driver){
        try {
            WebElement loginElement = new WebDriverWait(driver, Duration.ofSeconds(10).toSeconds()).
                    until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#id_login")));
            loginElement.sendKeys(userName);
            driver.findElement(By.cssSelector("#id_password")).sendKeys(password);
            driver.findElement(By.cssSelector("#id_password")).sendKeys(Keys.ENTER);
        } catch(Exception e){
            e.printStackTrace();
            System.err.println("couldn't sign in");
            return false;
        }
        return true;
    }

     private void initialize(String credentialsPath) throws Exception{
        Scanner reader = new Scanner(new File(credentialsPath));
        int i = 0;
        while(reader.hasNextLine()){
            if(i == 0){
                this.userName = reader.nextLine();
            } else {
                password = reader.nextLine();
            }
            i++;
        }

        if(i != 2){
            System.err.println("file should include username and password on two seperate lines");
        }
    }

    public String getPassword(){
        return this.password;
    }

    public String getUserName(){
        return this.userName;
    }


}
