package org.iru.translation.io;

import java.io.Reader;
import org.apache.commons.configuration2.PropertiesConfiguration.PropertiesReader;

/**
 * This class encode linebreaks written as "\n"
 */
public class LineBreakPropertiesReader extends PropertiesReader {

    public LineBreakPropertiesReader(Reader reader) {
        super(reader);
    }

    @Override
    protected void initPropertyValue(String value) {
        super.initPropertyValue(value.replace("\\n", "<LINEBREAK>"));
    }

}
