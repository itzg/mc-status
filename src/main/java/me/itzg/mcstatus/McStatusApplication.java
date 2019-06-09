package me.itzg.mcstatus;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;

@SpringBootApplication(exclude = {GsonAutoConfiguration.class})
public class McStatusApplication {

	public static void main(String[] args) {
		final SpringApplication app = new SpringApplication(McStatusApplication.class);

		for (String arg : args) {
			if (arg.equals("--one-shot")) {
				app.setAdditionalProfiles("one-shot");
				app.setBannerMode(Mode.OFF);
			}
		}

		app.run(args);
	}
}
