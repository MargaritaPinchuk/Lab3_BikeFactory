package bikefactory.io.xml;


public enum XmlParserType {
    DOM("DOM"),
    SAX("SAX"),
    STAX("StAX");

    private final String title;

    XmlParserType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
