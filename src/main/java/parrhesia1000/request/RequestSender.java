package parrhesia1000.request;

import org.springframework.web.socket.WebSocketSession;

public interface RequestSender {

    void sendRequest(WebSocketSession session, Request request);
}
