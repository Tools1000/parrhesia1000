package parrhesia1000;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.dto.Request;
import parrhesia1000.request.RequestFactory;

import java.util.List;

/**
 * Sends an event to request author's metadata.
 */
@RequiredArgsConstructor
@Slf4j
public class AuthorsMetadataRequester {

    private final ObjectMapper objectMapper;

    private final WebSocketSession session;

    public void requestAuthorsMetadata(String author) {
        requestAuthorsMetadata(List.of(author));
    }

    public void requestAuthorsMetadata(List<String> authors) {
        try {
            Request req = RequestFactory.buildGetAuthorsMetadataRequest(authors);
            String json = objectMapper.writeValueAsString(req);
            log.info("Requesting metadata for {} authors", authors.size());
            log.debug("Sending {}", json);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
