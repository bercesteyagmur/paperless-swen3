package at.ac.fhtw.swen3.paperless;

import org.springframework.boot.SpringApplication; // has the run(...) method that actually starts the whole Spring application
import org.springframework.boot.autoconfigure.SpringBootApplication; // tells Spring "auto-configure everything, and scan this package + subpackages for components"

/**
 * This is the entry point of the whole backend
 */
@SpringBootApplication
public class PaperlessApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaperlessApplication.class, args);
	}

}
