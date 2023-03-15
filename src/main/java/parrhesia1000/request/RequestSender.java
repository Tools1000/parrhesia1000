package parrhesia1000.request;

import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.dto.CloseRequest;
import parrhesia1000.dto.Request;

public interface RequestSender {

    void sendRequest(WebSocketSession session, Object request);


}
