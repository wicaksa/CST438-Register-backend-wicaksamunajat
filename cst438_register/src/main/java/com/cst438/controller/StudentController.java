package com.cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController
{
   @Autowired
   CourseRepository courseRepository;
   
   @Autowired
   StudentRepository studentRepository;
   
   @Autowired
   EnrollmentRepository enrollmentRepository;
   
   @Autowired
   GradebookService gradebookService;
   
   /*
    * Gets all the students from the repository
    * 
    */
   @GetMapping("/getAllStudents")
   public Iterable<Student> retrieveAllStudents() {
      Iterable<Student> students = studentRepository.findAll();
      return students;
   }
   
   /*
    * Add student to the database.
    * As an administrator, I can add a student to the system.  
    * I input the student email and name. 
    * The student email must not already exists in the system.
    * 
    */
   @PostMapping("/addStudent")
   @Transactional
   public Student addStudent( @RequestBody Student s  ) { 
      
      String student_email = s.getEmail(); 
      String student_name = s.getName();
      Student existing_student = studentRepository.findByEmail(student_email);
      
      // Check to make sure email and name aren't null.
      // Check to make sure student email is not already in the database.
      if (student_email!= null && student_name!=null && existing_student == null) {
         
         // Create a new student object
         Student newStudent = new Student();
         newStudent = s;
         s.setStatusCode(0);
         
         // Add student to the database and save.
         Student savedStudent = studentRepository.save(newStudent);
         return savedStudent;
      
      } else {
         throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student email already exists.");
      }
   }
   
   /*
    * Add a Student Hold.
    * Adds 1 to a student hold value.
    * 
    */
   @PutMapping("/addHold/{student_id}")
   @Transactional
   public Student addHold( @PathVariable int student_id) {
      Student s = studentRepository.findById(student_id).orElse(null);
      
      if (s != null) {
         s.setStatusCode(s.getStatusCode()+1);
         studentRepository.save(s);
         return s;
      }
     
      else {
         throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.");
      }
   }
   
   /*
    * Removes a student hold.
    * Changes status_code to 0.
    * 
    */
   @PutMapping("/removeHold/{student_id}")
   @Transactional
   public Student removeHold( @PathVariable int student_id) {
      Student s = studentRepository.findById(student_id).orElse(null);
      
      if (s != null) {
         s.setStatusCode(0);
         studentRepository.save(s);
         return s;
      }
     
      else {
         throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student doesn't exist.");
      }
   }
}
