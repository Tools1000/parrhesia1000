package parrhesia1000.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.dto.CloseRequest;
import parrhesia1000.dto.Request;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRequestSender implements RequestSender {

    private final ObjectMapper mapper;




    public void sendRequest(WebSocketSession session, Object request) {
        try {
            String json = mapper.writeValueAsString(request);
            log.debug("Sending {}", json);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
