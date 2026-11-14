package at.ac.fhtw.swen3.paperless.indexingworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//going to extend this worker to GenAi worker in sprint 5

public class IndexingWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IndexingWorkerApplication.class, args);
    }
}
