package com.joaovictor.libraryapi;

import com.joaovictor.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryapiApplication {

//	@Autowired
//	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

//	@Bean
//	public CommandLineRunner runner() {
//		return args -> {
//			List<String> emails = List.of("email@email.com");
//			emailService.sendEmails(emails,"Testando envio de emails.");
//			System.out.println("EMAILS ENVIADOS");
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryapiApplication.class, args);
	}

}
