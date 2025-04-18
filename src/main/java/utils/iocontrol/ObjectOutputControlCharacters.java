package utils.iocontrol;

/**
 * A class for mapping characters to control the output of objects.
 */
public record ObjectOutputControlCharacters() {

    /**
     * The character sequence used to separate the key and value of a map entry.
     */
    public static final String DELIMITER_STRING = "\u001B\u001B\u001B"; // ESC ESC ESC

    /**
     * The character sequence used to separate map entries (features) of an object.
     */
    public static final String SEPARATOR_STRING = "\u001A\u001A\u001A"; // SUB SUB SUB

    /**
     * The character sequence used to separate elements within a List<String>.
     * Using Unit Separator (US). Ensure this doesn't conflict with actual data.
     */
    public static final String LIST_ELEMENT_SEPARATOR = "\u001F"; // US (Unit Separator)
}