package org.iru.translation.io;

import java.io.Reader;
import java.io.Writer;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;

public class LineBreakIOFactory implements PropertiesConfiguration.IOFactory {

    @Override
    public PropertiesConfiguration.PropertiesWriter createPropertiesWriter(Writer out, ListDelimiterHandler handler) {
         return new LineBreakPropertiesWriter(out, handler);
    }

    @Override
    public PropertiesConfiguration.PropertiesReader createPropertiesReader(Reader in) {
       return new LineBreakPropertiesReader(in);
    }

}
