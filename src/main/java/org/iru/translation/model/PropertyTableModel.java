package org.iru.translation.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;
import org.iru.translation.gui.Action;

public class PropertyTableModel extends AbstractTableModel {
    
    private List<Property> list = new ArrayList<>(0);
    private List<Property> filteredlist = new ArrayList<>(0);
    private String fromFileName = "No FROM file selected";
    private String toFileName = "No TO file selected";
    private boolean filterDeleted = false;
    private boolean filterAdded = false;
    private boolean filterUnstranslated = false;

    public PropertyTableModel() {
    }
    
    public void setModel(List<Property> list) {
        this.list = list;
        doFilter();
    }
    
    public void setModel(List<Property> list, String fromFileName, String toFileName) {
        this.list = list;
        this.fromFileName = fromFileName;
        this.toFileName = toFileName;
        fireTableStructureChanged();
        doFilter();
    }
    
    public void setModel(List<Property> list, String fromFileName) {
        this.list = list;
        this.filteredlist = new LinkedList<>(list);
        this.fromFileName = fromFileName;
        fireTableStructureChanged();
    }
    
    public Property getModel(int row) {
        return filteredlist.get(row);
    }
    
    @Override
    public int getRowCount() {
        return filteredlist.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        String result = null;
        switch (columnIndex) {
            case 0:
                result = "Key";
                break;
            case 1:
                result = fromFileName;
                break;
            case 2:
                result = toFileName;
                break;
        }
        return result;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        switch (columnIndex) {
            case 0:
                result = filteredlist.get(rowIndex).getKey();
                break;
            case 1:
                result = filteredlist.get(rowIndex).getValueFrom();
                break;
            case 2:
                result = filteredlist.get(rowIndex).getValueTo();
                break;
        }
        return result;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            filteredlist.get(rowIndex).setValueTo((String)aValue);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }
    
    public void toggleFilterDeleted() {
        filterDeleted = !filterDeleted;
        doFilter();
    }

    public void toggleFilterAdded() {
        filterAdded = !filterAdded;
        doFilter();
    }

    public void toggleFilterUntranslated() {
        filterUnstranslated = !filterUnstranslated;
        doFilter();
    }

    public boolean isFilterDeleted() {
        return filterDeleted;
    }

    public boolean isFilterAdded() {
        return filterAdded;
    }

    public boolean isFilterUnstranslated() {
        return filterUnstranslated;
    }
    
    private void doFilter() {
        filteredlist = list.stream()
                .filter(p -> isFiltered(p))
                .collect(Collectors.toList());		
        fireTableDataChanged();
    }
    
    private boolean isFiltered(Property p) {
        if (!filterDeleted && !filterAdded && !filterUnstranslated) {
            return true;
        }
        return (p.getAction() == Action.DELETED && filterDeleted)
                || (p.getAction() == Action.ADDED && filterAdded)
                || (p.getAction() == Action.UNTRANSLATED && filterUnstranslated);
    }

}
