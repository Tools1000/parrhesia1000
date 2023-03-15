package parrhesia1000;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import parrhesia1000.config.AppConfig;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.request.RequestFactory;
import parrhesia1000.request.RequestSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Slf4j
@Component
public class TestSessionCallbackHandler extends TextWebSocketHandler {

    private final AppConfig appConfig;

    private final ObjectMapper mapper;

    private final RequestSender requestSender;

    private AtomicLong eventCounter = new AtomicLong();

    private LocalDateTime oldest = LocalDateTime.MAX;

    private LocalDateTime newest = LocalDateTime.MIN;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established with session: {}", session);
        requestSender.sendRequest(session, RequestFactory.buildPublicFeedRequest());
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection closed {}", session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

//        log.debug("Message payload: {}", message.getPayload());

        Event event = mapper.readValue(message.getPayload().getBytes(StandardCharsets.UTF_8), Event.class);

        log.debug("Received event {}: {}", eventCounter.incrementAndGet(), event);

        if(event.getData() == null){
            log.debug("Ignoring {}", event);
            return;
        }

        LocalDateTime eventTime = ParrhesiaUtils.parse(event.getData().getCreatedAt());
        if(eventTime.isBefore(oldest)){
            oldest = eventTime;
        }
        if(eventTime.isAfter(newest)){
            newest = eventTime;
        }
        log.debug("Oldest: {}, current: {}, newest: {}", oldest, ParrhesiaUtils.parse(event.getData().getCreatedAt()), newest);

        int wait = 0;
    }


}
