package at.ac.fhtw.swen3.paperless.genaiworker.service;

import at.ac.fhtw.swen3.paperless.genaiworker.exception.GeminiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String summarize(String text) {
        try {
            return chatClient.prompt()
                    .user("Summarize the following document briefly:\n\n" + text)
                    .call()
                    .content();
        } catch (RuntimeException exception) {
            logger.error("Could not generate summary", exception);
            throw new GeminiException("Could not generate summary", exception);
        }
    }
}
