package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.json.JacksonTester;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;


import com.cst438.controller.StudentController;

import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

import junit.framework.Assert;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent
{
   // Mock Test Variables 
   public static final int TEST_STUDENT_ID = 100;
   public static final String TEST_STUDENT_NAME ="TEST STUDENT";
   public static final String TEST_STUDENT_EMAIL_NEW = "MOCK@GMAIL.COM";
   public static final String TEST_STUDENT_EMAIL_OLD = "test@csumb.edu";
   public static final int TEST_STUDENT_STATUS_CODE = 0;
   public static final int TEST_STUDENT_STATUS_CODE_NOT_ZERO = 5;
   
   @MockBean
   CourseRepository courseRepository;

   @MockBean
   StudentRepository studentRepository;

   @MockBean
   EnrollmentRepository enrollmentRepository;

   @MockBean
   GradebookService gradebookService;

   @Autowired
   private MockMvc mvc;
   
   private JacksonTester<Student> jsonStudentAttempt;

   @BeforeEach
   public void setup() {
       JacksonTester.initFields(this, new ObjectMapper());
   }
   
   @Test
   public void Add_Student_Pass_NoDuplicateEmail() throws Exception {
      
      // Expected Input
      Student attempt = new Student();
      attempt.setStudent_id(TEST_STUDENT_ID);
      attempt.setName(TEST_STUDENT_NAME);
      attempt.setEmail(TEST_STUDENT_EMAIL_NEW);
      attempt.setStatusCode(TEST_STUDENT_STATUS_CODE);
      attempt.setStatus(null);
      
      
      // Expected Output
      Student expected = new Student();
      expected.setStudent_id(100);
      expected.setName("TEST STUDENT");
      expected.setEmail("MOCK@GMAIL.COM");
      expected.setStatusCode(0);
      expected.setStatus(null);
      
      // given  -- stubs for database repositories that return test data
      given(studentRepository.findById(100)).willReturn(Optional.of(expected));
      
      MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders 
               .post("/addStudent")
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStudentAttempt.write(expected).getJson()))
            .andReturn().getResponse();
      
      // Verify that the return status = OK (value 200).
      // Does not return anything in the body.
      assertEquals(200, response.getStatus());
   }
   
   @Test
   public void Add_Student_Fail_DuplicateEmailExists() throws Exception {
      
      // Expected Input
      Student attempt = new Student();
      attempt.setStudent_id(TEST_STUDENT_ID);
      attempt.setName(TEST_STUDENT_NAME);
      attempt.setEmail(TEST_STUDENT_EMAIL_NEW);
      attempt.setStatusCode(TEST_STUDENT_STATUS_CODE); //0
      attempt.setStatus(null);
      
      // Expected Output
      Student expected = new Student();
      expected.setStudent_id(100);
      expected.setName("TEST STUDENT");
      expected.setEmail("MOCK@GMAIL.COM");
      expected.setStatusCode(0);
      expected.setStatus(null);
      
      // given  -- stubs for database repositories that return test data
      given(studentRepository.findByEmail(TEST_STUDENT_EMAIL_NEW)).willReturn(expected);
      
      MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders 
               .post("/addStudent")
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStudentAttempt.write(attempt).getJson()))
            .andReturn().getResponse();
      
      // Verify that return status = 400.
      assertEquals(400, response.getStatus());
   }
   
   @Test
   public void Add_StudentHold_Pass_IncrementsStatusCodeByOne() throws Exception 
   {
      // Expected Input
      Student attempt = new Student();
      attempt.setStudent_id(TEST_STUDENT_ID);
      attempt.setName(TEST_STUDENT_NAME);
      attempt.setEmail(TEST_STUDENT_EMAIL_NEW);
      attempt.setStatusCode(TEST_STUDENT_STATUS_CODE);
      attempt.setStatus(null);
      
      // Expected Output
      Student expected = new Student();
      expected.setStudent_id(100);
      expected.setName("TEST STUDENT");
      expected.setEmail("MOCK@GMAIL.COM");
      expected.setStatusCode(1);
      expected.setStatus(null);
      
      // given  -- stubs for database repositories that return test data
      given(studentRepository.findById(100)).willReturn(Optional.of(attempt));
      
      MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders 
               .put("/addHold/100")
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStudentAttempt.write(expected).getJson()))
            .andReturn().getResponse();
      
      // verify that return status = OK (value 200) 
      assertEquals(200, response.getStatus());  
      
      // verify that status code is 1
      Student result = fromJsonString(response.getContentAsString(), Student.class);
      assertEquals(1, result.getStatusCode());
   }
   
   @Test
   public void Add_StudentHold_Fail_NonexistentStudentID() throws Exception 
   {
      // Expected Input
      Student expected = new Student();
      expected.setStudent_id(100);
      expected.setName("TEST STUDENT");
      expected.setEmail("MOCK@GMAIL.COM");
      expected.setStatusCode(1);
      expected.setStatus(null);
      
      // Expected Output
      Student attempt = new Student();
      attempt.setStudent_id(TEST_STUDENT_ID);
      attempt.setName(TEST_STUDENT_NAME);
      attempt.setEmail(TEST_STUDENT_EMAIL_NEW);
      attempt.setStatusCode(TEST_STUDENT_STATUS_CODE);
      attempt.setStatus(null);
      
      // given  -- stubs for database repositories that return test data
      given(studentRepository.findById(1000)).willReturn(Optional.of(expected));
  
      MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders 
               .put("/addHold/100")
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStudentAttempt.write(attempt).getJson()))
            .andReturn().getResponse();
      
      // Verify that return status = 400.
      assertEquals(400, response.getStatus());              
   }
   
   @Test
   public void Remove_StudentHold_Pass_ChangeHoldStatusToZero() throws Exception 
   {
      // Expected Input
      Student attempt = new Student();
      attempt.setStudent_id(TEST_STUDENT_ID);
      attempt.setName(TEST_STUDENT_NAME);
      attempt.setEmail(TEST_STUDENT_EMAIL_NEW);
      attempt.setStatusCode(TEST_STUDENT_STATUS_CODE_NOT_ZERO);
      attempt.setStatus(null);
      
      // Expected Output
      Student expected = new Student();
      expected.setStudent_id(100);
      expected.setName("TEST STUDENT");
      expected.setEmail("MOCK@GMAIL.COM");
      expected.setStatusCode(0);
      expected.setStatus(null);
      
      // given  -- stubs for database repositories that return test data
      given(studentRepository.findById(100)).willReturn(Optional.of(expected));
  
      MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders 
               .put("/removeHold/100")
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStudentAttempt.write(attempt).getJson()))
            .andReturn().getResponse();
      
      // Verify that return status = OK (value 200).
      assertEquals(200, response.getStatus());  
      
      // Verify that status code in the response body is now 0.
      Student result = fromJsonString(response.getContentAsString(), Student.class);
      assertEquals(0, result.getStatusCode());
   }
   
   @Test
   public void Remove_StudentHold_Fail_NonexistentStudentID() throws Exception 
   {
      // Expected Input
      Student attempt = new Student();
      attempt.setStudent_id(TEST_STUDENT_ID);
      attempt.setName(TEST_STUDENT_NAME);
      attempt.setEmail(TEST_STUDENT_EMAIL_NEW);
      attempt.setStatusCode(TEST_STUDENT_STATUS_CODE_NOT_ZERO);
      attempt.setStatus(null);
      
      // Expected Output
      Student expected = new Student();
      expected.setStudent_id(100);
      expected.setName("TEST STUDENT");
      expected.setEmail("MOCK@GMAIL.COM");
      expected.setStatusCode(0);
      expected.setStatus(null);
      
      // given  -- stubs for database repositories that return test data
      given(studentRepository.findById(100)).willReturn(Optional.of(expected));
  
      MockHttpServletResponse response = mvc.perform(
            MockMvcRequestBuilders 
               .put("/removeHold/1000")
               .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStudentAttempt.write(attempt).getJson()))
            .andReturn().getResponse();
      
      // verify that return status = 400.
      assertEquals(400, response.getStatus()); 
      
      // verify that the response message is the same.
      assertEquals("Student doesn't exist.", response.getErrorMessage());
   }
   
   private static <T> T  fromJsonString(String str, Class<T> valueType ) {
      try {
         return new ObjectMapper().readValue(str, valueType);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
