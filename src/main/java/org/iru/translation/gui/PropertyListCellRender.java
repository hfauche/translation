package org.iru.translation.gui;

import org.iru.translation.properties.PropertyModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class PropertyListCellRender extends DefaultListCellRenderer {
    
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        PropertyModel.Property prop = (PropertyModel.Property) value;
        switch(prop.getAction()) {
            case ADDED:
                result.setBackground(new Color(173, 190, 255));
                break;
            case DELETED:
                result.setBackground(new Color(255, 205, 197));
                break;
            case UNTRANSLATED:
                result.setBackground(new Color(255, 249, 182));
                break;
        }
        return result;
    }

}
