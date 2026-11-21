package at.ac.fhtw.swen3.paperless.genaiworker.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String summarize(String text) {
        return chatClient.prompt()
                .user("Summarize the following document briefly:\n\n" + text)
                .call()
                .content();
    }
}