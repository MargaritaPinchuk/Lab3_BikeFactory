package bikefactory.io.json;


public class JsonParseException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int line;
    private final int column;

    public JsonParseException(String message, int line, int column) {
        super(message + " (строка " + line + ", позиция " + column + ")");
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
