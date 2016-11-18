package org.iru.translation.gui;

import org.iru.translation.properties.PropertyModel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class PropertyListCellRender extends DefaultListCellRenderer implements Colors {
    
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
                result.setBackground(ADDED_COLOR);
                break;
            case DELETED:
                result.setBackground(DELETED_COLOR);
                break;
            case UNTRANSLATED:
                result.setBackground(UNTRANSLATED_COLOR);
                break;
        }
        return result;
    }

}
