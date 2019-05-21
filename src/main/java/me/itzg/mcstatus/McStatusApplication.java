package me.itzg.mcstatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;

@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
public class McStatusApplication {

	public static void main(String[] args) {
		SpringApplication.run(McStatusApplication.class, args);
	}
}
