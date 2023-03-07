package parrhesia1000;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import parrhesia1000.dto.Author;
import parrhesia1000.event.Event;
import parrhesia1000.request.RequestFactory;
import parrhesia1000.request.RequestSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class TestSessionCallbackHandler extends TextWebSocketHandler {

    private final AppConfig appConfig;

    private final ObjectMapper mapper;

    private final RequestSender requestSender;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established with session: {}", session);
        requestSender.sendRequest(session, new RequestFactory().buildGetAuthorMetadataRequest(List.of("c5d33ae2ca60815d54f6f6ef4af3a426355665fb0a4bb384d28171e6b872c441")));
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection closed {}", session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

//        log.debug("Message payload: {}", message.getPayload());

        Event event = mapper.readValue(message.getPayload().getBytes(StandardCharsets.UTF_8), Event.class);

        log.debug("Received event: {}", event);

        if(!event.getEvent().equals("EOSE")) {
            Author author = mapper.readValue(event.getData().getContent(), Author.class);
            log.debug("Author: {}", author);

        }

    }


}
