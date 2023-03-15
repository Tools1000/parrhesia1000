package parrhesia1000;

import com.fasterxml.jackson.databind.ObjectMapper;
import fx1000.Alerts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import parrhesia1000.config.AppConfig;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.nostr.event.handler.*;
import parrhesia1000.request.RequestFactory;
import parrhesia1000.request.RequestSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Slf4j
@Component
public class SessionCallbackHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;

    private final List<EventHandler> eventHandlerList = new ArrayList<>();

    private final List<ConnectHandler> connectHandlerList = new ArrayList<>();

    private AtomicLong eventCounter = new AtomicLong();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established with session: {}", session);
        for(ConnectHandler connectHandler : connectHandlerList){
            connectHandler.connected(session);
        }
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection closed {}", session);
        for(ConnectHandler connectHandler : connectHandlerList){
            connectHandler.disconnected(session);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        Event event = mapper.readValue(message.getPayload().getBytes(StandardCharsets.UTF_8), Event.class);

        if("NOTICE".equals(event.getEvent())){
            log.warn("Received event {}: {}", eventCounter.incrementAndGet(), event);
        } else {
            log.debug("Received event {}: {}", eventCounter.incrementAndGet(), event);
        }

       for(EventHandler eventHandler : new ArrayList<>(eventHandlerList)){
           eventHandler.handleEvent(session, event);
       }

    }

    // Getter / Setter //


    public List<EventHandler> getEventHandlerList() {
        return eventHandlerList;
    }

    public List<ConnectHandler> getConnectHandlerList() {
        return connectHandlerList;
    }
}
