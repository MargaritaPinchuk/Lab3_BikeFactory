package bikefactory.io.xml;

import bikefactory.model.Bicycle;
import bikefactory.util.BicycleBuilder;
import bikefactory.util.ErrorLogger;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaxXmlReader extends AbstractXmlReader {
    public SaxXmlReader(String fileName) {
        super(fileName, XmlParserType.SAX);
    }

    @Override
    public List<Bicycle> read(ErrorLogger logger) throws IOException {
        BikeSaxHandler handler = new BikeSaxHandler();
        try (FileInputStream input = new FileInputStream(getFileName())) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            disableExternalDtd(factory);
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.setErrorHandler(handler);
            InputSource source = new InputSource(input);
            source.setSystemId(getFileName());
            xmlReader.parse(source);
        } catch (ParserConfigurationException e) {
            throw new IOException("не удалось настроить SAX-парсер: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new IOException("ошибка разбора XML в файле " + getFileName() + ": " + e.getMessage(), e);
        }

        List<Bicycle> result = new ArrayList<>();
        int number = 0;
        for (Map<String, String> fields : handler.getBikes()) {
            number++;
            Bicycle bike = toBicycle(fields, "велосипед #" + number, logger);
            if (bike != null) {
                result.add(bike);
            }
        }
        return result;
    }

    private static void disableExternalDtd(SAXParserFactory factory) {
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception ignored) {
        }
    }

    private static final class BikeSaxHandler extends DefaultHandler {
        private final List<Map<String, String>> bikes = new ArrayList<>();
        private Map<String, String> current;
        private String currentElement;
        private final StringBuilder text = new StringBuilder();
        private boolean rootSeen;

        List<Map<String, String>> getBikes() {
            return bikes;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            String name = elementName(localName, qName);
            if (!rootSeen) {
                rootSeen = true;
                if (!"bikes".equals(name) && !"bike".equals(name)) {
                    throw new SAXException("неожиданный корневой элемент <" + name + ">, ожидался <bikes>");
                }
            }
            if ("bike".equals(name)) {
                current = new LinkedHashMap<>();
                String id = attributes.getValue("", BicycleBuilder.ID);
                if (id != null && !id.trim().isEmpty()) {
                    current.put(BicycleBuilder.ID, id);
                }
                currentElement = null;
                text.setLength(0);
            } else if (current != null) {
                currentElement = name;
                text.setLength(0);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (current != null && currentElement != null) {
                text.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            String name = elementName(localName, qName);
            if ("bike".equals(name)) {
                if (current != null) {
                    bikes.add(current);
                }
                current = null;
                currentElement = null;
                text.setLength(0);
            } else if (current != null && name.equals(currentElement)) {
                current.put(name, text.toString().trim());
                currentElement = null;
                text.setLength(0);
            }
        }

        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }
}
