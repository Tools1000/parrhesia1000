package parrhesia1000;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parrhesia1000.nostr.event.Event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class EventFindAuthorsTest {

    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test01() throws IOException {
        Path file = Paths.get("src/test/resources/event-find-authors.json");
        String inJson = Files.readString(file);
        System.out.println(inJson);
        Event event = mapper.readValue(file.toFile(), Event.class);
        String outJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);
        assertEquals(inJson.replaceAll("\\s", ""), outJson.replaceAll("\\s", ""));
    }
}