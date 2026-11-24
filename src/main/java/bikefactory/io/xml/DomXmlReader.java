package bikefactory.io.xml;

import bikefactory.model.Bicycle;
import bikefactory.util.BicycleBuilder;
import bikefactory.util.ErrorLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DomXmlReader extends AbstractXmlReader {
    public DomXmlReader(String fileName) {
        super(fileName, XmlParserType.DOM);
    }

    @Override
    public List<Bicycle> read(ErrorLogger logger) throws IOException {
        Document document = loadDocument();
        Element root = document.getDocumentElement();
        if (root == null) {
            throw new IOException("в файле " + getFileName() + " нет корневого элемента");
        }

        List<Element> bikeElements = new ArrayList<>();
        String rootName = elementName(root.getLocalName(), root.getNodeName());
        if ("bike".equals(rootName)) {
            bikeElements.add(root);
        } else if ("bikes".equals(rootName)) {
            collectBikes(root, bikeElements);
        } else {
            throw new IOException("неожиданный корневой элемент <" + rootName
                    + "> в файле " + getFileName() + ", ожидался <bikes>");
        }

        List<Bicycle> result = new ArrayList<>();
        int number = 0;
        for (Element bikeElement : bikeElements) {
            number++;
            Map<String, String> fields = new LinkedHashMap<>();
            String id = bikeElement.getAttribute(BicycleBuilder.ID);
            if (id != null && !id.trim().isEmpty()) {
                fields.put(BicycleBuilder.ID, id);
            }
            for (String field : BicycleBuilder.FIELDS) {
                String value = childText(bikeElement, field);
                if (value != null) {
                    fields.put(field, value);
                }
            }
            Bicycle bike = toBicycle(fields, "велосипед #" + number, logger);
            if (bike != null) {
                result.add(bike);
            }
        }
        return result;
    }

    private Document loadDocument() throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setExpandEntityReferences(false);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            disableExternalDtd(factory);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new StrictErrorHandler());
            return builder.parse(new File(getFileName()));
        } catch (ParserConfigurationException e) {
            throw new IOException("не удалось настроить DOM-парсер: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new IOException("ошибка разбора XML в файле " + getFileName() + ": " + e.getMessage(), e);
        }
    }

    private static void disableExternalDtd(DocumentBuilderFactory factory) {
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ignored) {
            // не все реализации поддерживают эту настройку - для чтения это не критично
        }
    }

    private static void collectBikes(Element parent, List<Element> target) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                    && "bike".equals(elementName(node.getLocalName(), node.getNodeName()))) {
                target.add((Element) node);
            }
        }
    }

    private static String childText(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                    && name.equals(elementName(node.getLocalName(), node.getNodeName()))) {
                return node.getTextContent() == null ? "" : node.getTextContent().trim();
            }
        }
        return null;
    }

    private static final class StrictErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) {
            // предупреждения при чтении данных не важны
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
