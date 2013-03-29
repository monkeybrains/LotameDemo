/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.lotame;

import com.mycompany.lotame.api.Audience;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Chris
 */
public class AudienceTableModel extends AbstractTableModel {
    
    protected ArrayList<Audience> audiences;
    protected String[] columnNames = {
        "Name",
        "# Page Views",
        "Targeting Code",
        "# Uniques"
    };

    public AudienceTableModel(Collection<Audience> audiences) {
        this.audiences = new ArrayList<Audience>(audiences);
    }
    
    public int getRowCount() {
        return audiences.size();
    }

    public int getColumnCount() {
        return 4;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Audience audience = audiences.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return audience.getName();
            case 1:
                return audience.getPageViews();
            case 2:
                return audience.getTargetingCode();
            case 3:
                return audience.getUniques();
            default:
                return null;
        }
    }
    
    @Override
    public String getColumnName(int index) {
        return columnNames[index];
    }
    
}
