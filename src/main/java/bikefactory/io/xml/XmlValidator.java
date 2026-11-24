package bikefactory.io.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class XmlValidator {
    private XmlValidator() {
    }

    public static List<String> validateXsd(String xmlFileName, String schemaFileName) throws IOException {
        List<String> errors = new ArrayList<>();
        File xmlFile = requireXmlFile(xmlFileName);
        URL schemaUrl = resolveSchema(schemaFileName, xmlFileName);
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(schemaUrl);
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.setErrorHandler(new CollectingErrorHandler(errors));
            validator.validate(new StreamSource(xmlFile));
        } catch (SAXException e) {
            if (errors.isEmpty()) {
                errors.add(String.valueOf(e.getMessage()));
            }
        }
        return errors;
    }

    public static List<String> validateDtd(String xmlFileName, String dtdFileName) throws IOException {
        List<String> errors = new ArrayList<>();
        File xmlFile = requireXmlFile(xmlFileName);
        final URL dtdUrl = resolveSchema(dtdFileName, xmlFileName);
        final String dtdName = new File(dtdFileName).getName();
        try (FileInputStream input = new FileInputStream(xmlFile)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            XMLReader reader = factory.newSAXParser().getXMLReader();
            reader.setEntityResolver((publicId, systemId) -> {
                if (systemId != null && systemId.endsWith(dtdName)) {
                    return new InputSource(dtdUrl.openStream());
                }
                return null;
            });
            reader.setErrorHandler(new CollectingErrorHandler(errors));
            reader.setContentHandler(new DefaultHandler());
            InputSource source = new InputSource(input);
            source.setSystemId(xmlFile.getAbsolutePath());
            reader.parse(source);
        } catch (ParserConfigurationException | SAXException e) {
            if (errors.isEmpty()) {
                errors.add(String.valueOf(e.getMessage()));
            }
        }
        return errors;
    }

    private static URL resolveSchema(String schemaFileName, String xmlFileName) throws IOException {
        String name = schemaFileName == null ? "" : schemaFileName.trim();
        if (name.isEmpty()) {
            throw new FileNotFoundException("не задано имя файла схемы");
        }
        List<String> tried = new ArrayList<>();
        File direct = new File(name);
        if (direct.isAbsolute()) {
            if (direct.isFile()) {
                return direct.toURI().toURL();
            }
            tried.add(direct.getAbsolutePath());
        } else {
            File xmlDir = new File(xmlFileName).getAbsoluteFile().getParentFile();
            if (xmlDir != null) {
                File nearXml = new File(xmlDir, name);
                if (nearXml.isFile()) {
                    return nearXml.toURI().toURL();
                }
                tried.add(nearXml.getAbsolutePath());
            }
            File inWorkDir = direct.getAbsoluteFile();
            if (inWorkDir.isFile()) {
                return inWorkDir.toURI().toURL();
            }
            tried.add(inWorkDir.getAbsolutePath());
            File inResources = new File("src/main/resources", name).getAbsoluteFile();
            if (inResources.isFile()) {
                return inResources.toURI().toURL();
            }
            tried.add(inResources.getAbsolutePath());
        }
        URL fromClasspath = XmlValidator.class.getResource("/" + name);
        if (fromClasspath != null) {
            return fromClasspath;
        }
        tried.add("classpath:/" + name);
        throw new FileNotFoundException("не найден файл схемы '" + name + "'. Искали: "
                + String.join("; ", tried));
    }

    private static File requireXmlFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.isFile()) {
            throw new FileNotFoundException("не найден XML-файл: " + file.getAbsolutePath());
        }
        return file;
    }

    private static final class CollectingErrorHandler implements ErrorHandler {
        private final List<String> errors;

        CollectingErrorHandler(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) {
            errors.add(describe(exception));
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            errors.add(describe(exception));
            throw exception;
        }

        private static String describe(SAXParseException exception) {
            String message = exception.getMessage();
            if (exception.getLineNumber() > 0) {
                return "строка " + exception.getLineNumber() + ", позиция "
                        + exception.getColumnNumber() + ": " + message;
            }
            return String.valueOf(message);
        }
    }
}
