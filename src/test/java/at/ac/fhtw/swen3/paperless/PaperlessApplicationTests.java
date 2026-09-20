package at.ac.fhtw.swen3.paperless;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest starts the ENTIRE real Spring application (all beans,
// all configuration) just to check that nothing crashes during startup.
// This is a smoke test - it does not check any business logic, only that
// the whole wiring (entities, repositories, services, database connection)
// is valid.
@SpringBootTest
class PaperlessApplicationTests {

	@Test
	void contextLoads() {
	}

}
