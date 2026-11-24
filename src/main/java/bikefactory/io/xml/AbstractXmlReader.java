package bikefactory.io.xml;

import bikefactory.io.FileHandler;
import bikefactory.model.Bicycle;
import bikefactory.util.BicycleBuilder;
import bikefactory.util.ErrorLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class AbstractXmlReader extends FileHandler {
    private final XmlParserType parserType;

    protected AbstractXmlReader(String fileName, XmlParserType parserType) {
        super(fileName);
        this.parserType = parserType;
    }

    @Override
    public String getFormatName() {
        return "xml";
    }

    public XmlParserType getParserType() {
        return parserType;
    }

    public abstract List<Bicycle> read(ErrorLogger logger) throws IOException;

    protected Bicycle toBicycle(Map<String, String> fields, String context, ErrorLogger logger) {
        try {
            return BicycleBuilder.create(fields);
        } catch (IllegalArgumentException e) {
            logger.log(parserType.getTitle() + ", " + context + ": " + e.getMessage(), fields.toString());
            return null;
        }
    }

    protected static String elementName(String localName, String qName) {
        if (localName != null && !localName.isEmpty()) {
            return localName;
        }
        int colon = qName == null ? -1 : qName.lastIndexOf(':');
        return colon >= 0 ? qName.substring(colon + 1) : qName;
    }
}
