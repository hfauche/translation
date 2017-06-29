package org.iru.translation.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import static org.iru.translation.gui.Action.UNTRANSLATED;
import static org.iru.translation.gui.Colors.ADDED_COLOR;
import static org.iru.translation.gui.Colors.DELETED_COLOR;
import static org.iru.translation.gui.Colors.UNTRANSLATED_COLOR;
import org.iru.translation.model.PropertyTableModel;
import org.iru.translation.model.PropertyTableModel.Property;
import static org.iru.translation.gui.Action.OBSOLETE;
import static org.iru.translation.gui.Action.MISSING;

public class PropertyCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {
        Component result = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        PropertyTableModel model = (PropertyTableModel)table.getModel();
        Property prop = model.getModel(row);
        switch(prop.getAction()) {
            case OBSOLETE:
                result.setBackground(ADDED_COLOR);
                break;
            case MISSING:
                result.setBackground(DELETED_COLOR);
                break;
            case UNTRANSLATED:
                result.setBackground(UNTRANSLATED_COLOR);
                break;
            default:
                result.setBackground(table.getBackground());
                break;
        }
        if (table.getSelectedRow() == row) {
            final Color background = result.getBackground().darker();
            result.setBackground(background);
        }
        return result;
    }
}
