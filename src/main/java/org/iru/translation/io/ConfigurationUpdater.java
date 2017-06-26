package org.iru.translation.io;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;

public class ConfigurationUpdater implements PropertyChangeListener {
    
    private final Configuration configuration;

    public ConfigurationUpdater(Configuration configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public void notifyPropertyValueCĥange(PropertyChangeListenerAble p) {
        //set to null if blank to trigger a delete of the property
        configuration.setProperty(p.getKey(), StringUtils.defaultIfBlank(p.getValueTo(), null));
    }

}
