package org.iru.translation.io;

import org.apache.commons.configuration2.PropertiesConfigurationLayout;

public class EpdPropertiesConfigurationLayout extends PropertiesConfigurationLayout {

    @Override
    public String getGlobalSeparator()
    {
        return "=";
    }
}
