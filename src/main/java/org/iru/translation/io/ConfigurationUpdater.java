package org.iru.translation.io;

import org.apache.commons.configuration2.Configuration;
import org.iru.translation.model.Property;

public class ConfigurationUpdater implements PropertyChangeListener {
    
    private final Configuration configuration;

    public ConfigurationUpdater(Configuration configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public void notifyvalueCĥange(Property p) {
        configuration.setProperty(p.getKey(), p.getValueTo());
    }

}
