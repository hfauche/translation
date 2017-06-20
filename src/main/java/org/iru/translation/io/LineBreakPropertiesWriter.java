package org.iru.translation.io;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;

/**
 * This class decode previously encoded linebreaks
 * It also does not escape property value and generate Windows
 * compatible line breaks
 */
public class LineBreakPropertiesWriter extends PropertiesConfiguration.PropertiesWriter {

    public LineBreakPropertiesWriter(Writer writer, ListDelimiterHandler delHandler) {
        super(writer, delHandler);
    }

    @Override
    public void writeProperty(String key, Object value, boolean forceSingleLine) throws IOException {
        write(key);
        write(fetchSeparator(key, value));
        String v = ((String)value).replace("<LINEBREAK>", "\\n");
        write(v);
        writeln(null);
    }

    @Override
    public String getLineSeparator() {
        return "\r\n"; //for Windows compatibility
    }
}
