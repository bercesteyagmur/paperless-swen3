package at.ac.fhtw.swen3.paperless.genaiworker.service;

import at.ac.fhtw.swen3.paperless.genaiworker.exception.GeminiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec request;

    @Mock
    private ChatClient.CallResponseSpec response;

    private GeminiService geminiService;

    @BeforeEach
    void setUp() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(request);
        when(request.user(anyString())).thenReturn(request);
        when(request.call()).thenReturn(response);

        geminiService = new GeminiService(chatClientBuilder);
    }

    @Test
    void returnsGeneratedSummary() {
        when(response.content()).thenReturn("Short summary");

        String summary = geminiService.summarize("Document text");

        assertThat(summary).isEqualTo("Short summary");
        verify(request).user("Summarize the following document briefly:\n\nDocument text");
    }

    @Test
    void throwsGeminiExceptionWhenApiFails() {
        RuntimeException apiException = new RuntimeException("API unavailable");
        when(response.content()).thenThrow(apiException);

        assertThatThrownBy(() -> geminiService.summarize("Document text"))
                .isInstanceOf(GeminiException.class)
                .hasCause(apiException);
    }
}
