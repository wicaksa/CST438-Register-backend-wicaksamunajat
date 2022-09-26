package com.cst438.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.EnrollmentDTO;


public class GradebookServiceREST extends GradebookService {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	String gradebook_url;
	
	public GradebookServiceREST() {
		System.out.println("REST grade book service");
	}

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		
		//TODO  complete this method in homework 4
	   EnrollmentDTO enrollment = new EnrollmentDTO();
	   enrollment.studentEmail = student_email;
	   enrollment.studentName = student_name;
	   enrollment.course_id = course_id;
	   
	   // Debug
	   System.out.println("Post to Gradebook: " + enrollment);
	   
	   // Response from server
	   EnrollmentDTO response = restTemplate.postForObject(gradebook_url + "/enrollment", enrollment, EnrollmentDTO.class);
	   
	   // Debug
	   System.out.println("Response from Gradebook Server: " + response);
		
	}

}
