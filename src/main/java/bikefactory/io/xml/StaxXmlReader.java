package bikefactory.io.xml;

import bikefactory.model.Bicycle;
import bikefactory.util.BicycleBuilder;
import bikefactory.util.ErrorLogger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StaxXmlReader extends AbstractXmlReader {
    public StaxXmlReader(String fileName) {
        super(fileName, XmlParserType.STAX);
    }

    @Override
    public List<Bicycle> read(ErrorLogger logger) throws IOException {
        List<Map<String, String>> raw = new ArrayList<>();
        try (InputStream input = new FileInputStream(getFileName())) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                readDocument(reader, raw);
            } finally {
                reader.close();
            }
        } catch (XMLStreamException e) {
            throw new IOException("ошибка разбора XML в файле " + getFileName() + ": " + e.getMessage(), e);
        }

        List<Bicycle> result = new ArrayList<>();
        int number = 0;
        for (Map<String, String> fields : raw) {
            number++;
            Bicycle bike = toBicycle(fields, "велосипед #" + number, logger);
            if (bike != null) {
                result.add(bike);
            }
        }
        return result;
    }

    private static void readDocument(XMLStreamReader reader, List<Map<String, String>> raw)
            throws XMLStreamException, IOException {
        boolean rootSeen = false;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event != XMLStreamConstants.START_ELEMENT) {
                continue;
            }
            String name = reader.getLocalName();
            if (!rootSeen) {
                rootSeen = true;
                if (!"bikes".equals(name) && !"bike".equals(name)) {
                    throw new IOException("неожиданный корневой элемент <" + name + ">, ожидался <bikes>");
                }
            }
            if ("bike".equals(name)) {
                raw.add(readBike(reader));
            }
        }
    }

    private static Map<String, String> readBike(XMLStreamReader reader) throws XMLStreamException {
        Map<String, String> fields = new LinkedHashMap<>();
        String id = reader.getAttributeValue(null, BicycleBuilder.ID);
        if (id != null && !id.trim().isEmpty()) {
            fields.put(BicycleBuilder.ID, id);
        }
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String field = reader.getLocalName();
                String value = reader.getElementText();
                fields.put(field, value == null ? "" : value.trim());
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                break;
            }
        }
        return fields;
    }
}
