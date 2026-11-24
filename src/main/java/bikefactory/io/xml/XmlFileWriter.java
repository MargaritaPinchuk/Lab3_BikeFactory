package bikefactory.io.xml;

import bikefactory.io.FileHandler;
import bikefactory.model.Bicycle;
import bikefactory.util.BicycleBuilder;
import bikefactory.util.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;


public class XmlFileWriter extends FileHandler {
    public static final String SCHEMA_FILE_NAME = "bikes.xsd";
    public static final String DTD_FILE_NAME = "bikes.dtd";

    private static final String SCHEMA_REFERENCE = "src/main/resources/" + SCHEMA_FILE_NAME;

    private static final String XSI_NAMESPACE = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

    private final boolean withDoctype;

    public XmlFileWriter(String fileName) {
        this(fileName, false);
    }

    public XmlFileWriter(String fileName, boolean withDoctype) {
        super(fileName);
        this.withDoctype = withDoctype;
    }

    @Override
    public String getFormatName() {
        return "xml";
    }

    public boolean isWithDoctype() {
        return withDoctype;
    }

    public void write(Collection<Bicycle> bikes) throws IOException {
        File target = new File(getFileName());
        File parent = target.getAbsoluteFile().getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().newDocument();

            Element root = document.createElement("bikes");
            document.appendChild(root);
            if (!withDoctype) {
                // подсказка редакторам, по какой схеме проверять файл
                root.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", XSI_NAMESPACE);
                root.setAttributeNS(XSI_NAMESPACE, "xsi:noNamespaceSchemaLocation", SCHEMA_REFERENCE);
            }

            for (Bicycle bike : bikes) {
                Element bikeElement = document.createElement("bike");
                bikeElement.setAttribute(BicycleBuilder.ID, String.valueOf(bike.getId()));
                appendText(document, bikeElement, BicycleBuilder.TYPE, bike.getType());
                appendText(document, bikeElement, BicycleBuilder.MODEL, bike.getModel());
                appendText(document, bikeElement, BicycleBuilder.WHEEL_DIAMETER,
                        String.valueOf(bike.getWheelDiameter()));
                appendText(document, bikeElement, BicycleBuilder.GEARS, String.valueOf(bike.getGears()));
                appendText(document, bikeElement, BicycleBuilder.WEIGHT, formatNumber(bike.getWeight()));
                appendText(document, bikeElement, BicycleBuilder.RELEASE_DATE,
                        Validator.formatIsoDate(bike.getReleaseDate()));
                appendText(document, bikeElement, BicycleBuilder.PRICE, formatNumber(bike.getPrice()));
                root.appendChild(bikeElement);
            }

            save(document, target);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new IOException("не удалось записать XML в файл " + getFileName() + ": " + e.getMessage(), e);
        }
    }

    private static void appendText(Document document, Element parent, String name, String value) {
        Element element = document.createElement(name);
        element.setTextContent(value == null ? "" : value);
        parent.appendChild(element);
    }

    private static String formatNumber(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private void save(Document document, File target) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        if (withDoctype) {
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD_FILE_NAME);
        }
        try {
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (IllegalArgumentException ignored) {
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8)) {
            transformer.transform(new DOMSource(document), new StreamResult(writer));
        } catch (IOException e) {
            throw new TransformerException(e);
        }
    }
}
