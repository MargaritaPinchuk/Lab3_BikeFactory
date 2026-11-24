package bikefactory.io.json;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonParserTest {

    @Test
    public void parse_object_returnsMapWithCorrectTypes() throws JsonParseException {
        Object result = JsonParser.parse("{\"id\": 1, \"model\": \"Stels\", \"price\": 15990.5}");
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals(1.0, map.get("id"));
        assertEquals("Stels", map.get("model"));
        assertEquals(15990.5, map.get("price"));
    }

    @Test
    public void parse_array_returnsListOfMaps() throws JsonParseException {
        Object result = JsonParser.parse("[{\"id\": 1}, {\"id\": 2}]");
        assertTrue(result instanceof List);
        assertEquals(2, ((List<?>) result).size());
    }

    @Test(expected = JsonParseException.class)
    public void parse_malformedJson_throwsJsonParseException() throws JsonParseException {
        JsonParser.parse("{\"id\": }");
    }

    @Test
    public void parse_escapedCharactersInString_areDecoded() throws JsonParseException {
        Object result = JsonParser.parse("{\"text\": \"line1\\nline2\"}");
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("line1\nline2", map.get("text"));
    }

    @Test(expected = JsonParseException.class)
    public void parse_emptyText_throws() throws JsonParseException {
        JsonParser.parse("");
    }
}
