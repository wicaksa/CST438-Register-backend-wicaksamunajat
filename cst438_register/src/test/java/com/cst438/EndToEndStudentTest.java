package com.cst438;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@SpringBootTest
public class EndToEndStudentTest
{
   // Location of the chrome driver
   public static final String CHROME_DRIVER_FILE_LOCATION = "/usr/local/bin/chromedriver";
   
   // Server for the React front end
   public static final String URL = "http://localhost:3000";
   
   // Test Student Info
   public static final int TEST_STUDENT_ID = 100;
   
   public static final String TEST_STUDENT_NAME ="Rocky Maivia";
   
   public static final String TEST_STUDENT_EMAIL = "RockyMaivia@gmail.com";
   
   public static final String TEST_STUDENT_NAME_EXIST ="test";
   
   public static final String TEST_STUDENT_EMAIL_EXIST = "test@csumb.edu";
   
   
   public static final int TEST_STUDENT_STATUS_CODE = 0;

   // Sleep duration
   public static final int SLEEP_DURATION = 1000; // 1 second. 
   
   /*
    * When running in @SpringBootTest environment, database repositories can be used
    * with the actual database.
    */
   
   @Autowired
   CourseRepository courseRepository;

   @Autowired
   StudentRepository studentRepository;

   @Autowired
   EnrollmentRepository enrollmentRepository;
   
   /*
    * Add Test Student to studentRepository with corresponding information above
    */
   
   @Test
   public void addStudentTest() throws Exception {
      
      // Update property name for browser
      System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
      
      // Update the class chromedriver for your chromedriver
      WebDriver driver = new ChromeDriver();
      
      // Puts an implicit wait for 10 seconds before throwing exception
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
      
      try {
         
         // Get the front end 
         driver.get(URL);
         
         // Must wait for the page to load
         Thread.sleep(SLEEP_DURATION);
         
         // Locate and click Add Student
         driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/a[2]")).click();
         Thread.sleep(SLEEP_DURATION);
         
         // Enter Name and Email
         driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_STUDENT_NAME);
         driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_STUDENT_EMAIL);
        
         // Click Add Student Button
         driver.findElement(By.xpath("//button")).click();
         Thread.sleep(SLEEP_DURATION);
         
         /*
          * verify that new student shows in student.
          * 
          */ 
       
          Student student = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
          
          // List<WebElement> elements  = driver.findElements(By.xpath("//div[@data-field='email']/div[@class='MuiDataGrid-cellContent']"));
          WebElement element  = driver.findElement(By.xpath("//input[@name='email']"));
          
          boolean found = false;
          // System.out.println("TEST"); // debug
          
          // System.out.println(element.getAttribute("value")); // for debug
          if (element.getAttribute("value").equals(TEST_STUDENT_EMAIL)) {
             found = true;
          }
          assertTrue( found, "Student added but not listed in students.");
          
          // verify that enrollment row has been inserted to database.
          Student s = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
          assertNotNull(s, "Student enrollment found in database.");
         
         
      } catch (Exception ex) {
         throw ex;
         
      } finally {
         // Clean up database
         Student s = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
         if (s != null)
            studentRepository.delete(s);

         driver.quit();
      }
   }
   
}
