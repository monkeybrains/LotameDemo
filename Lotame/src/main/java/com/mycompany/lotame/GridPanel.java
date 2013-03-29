/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.lotame;

import com.mycompany.lotame.api.Audience;
import com.mycompany.lotame.api.LotameAPI;
import com.mycompany.lotame.api.LotameAPI.Direction;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author Chris
 */
public class GridPanel extends JPanel {
    
    private static final String CLIENT_ID = "LAME";
    private static final int PAGE_COUNT = 20;
    
    private JComboBox options;
    private JComboBox direction;
    private JTable table;
    private final GridPanelDelegate delegate;
    
    private enum SortDirection {
        ASCENDING("Ascending", "ASC"),
        DESCENDING("Descending", "DESC");
        
        private String name;
        private String value;
        
        private SortDirection(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static String getValue(String option) {
            // This could be much better...
            if (option.equals(ASCENDING.toString())) {
                return ASCENDING.value;
            } else if (option.equals(DESCENDING.toString())) {
                return DESCENDING.value;
            } else {
                return "";
            }
        }
    }
    
    private enum Options {
        NAME("Name", "audienceName"),
        UNIQUES("Uniques", "uniques");
        
        private String name;
        private String value;
        
        private Options(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static String getValue(String option) {
            // This could be much better...
            if (option.equals(NAME.toString())) {
                return NAME.value;
            } else if (option.equals(UNIQUES.toString())) {
                return UNIQUES.value;
            } else {
                return "";
            }
        }
    }
    
    public GridPanel(final GridPanelDelegate delegate) {
        super(new BorderLayout());
        
        this.delegate = delegate;
        
        // Top row
        options = new JComboBox(new String[] { Options.UNIQUES.toString(), Options.NAME.toString() });
        direction = new JComboBox(new String[] { Direction.ASCENDING.toString(), Direction.DESCENDING.toString() });
        direction.setSelectedItem(Direction.DESCENDING.toString()); // Default descending
        
        final GridPanel self = this;
        
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.add(new JLabel("Order By"));
        panel.add(options);
        panel.add(direction);
        panel.add(new JButton("Refresh") {{
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    self.loadGrid();
                }
            });
        }});
        add(panel, BorderLayout.NORTH);
        
        // Bottom row (grid)
        table = new JTable();
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        this.setSize(new Dimension(500, 300));
    }
    
    public void loadGrid() {
        Collection<Audience> audiences = null;
        try {
            audiences = App.api.getAudiences(CLIENT_ID, PAGE_COUNT, Options.getValue((String)options.getSelectedItem()), Direction.getDirection((String)direction.getSelectedItem()));
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.WARNING, null, ex);
            
            // If anything bad happens, assume we're logged out.  This is lame, but it's quick...
            delegate.onLogout();
        }

        if (audiences == null) {
            audiences = new ArrayList<Audience>();
        }

        table.setModel(new AudienceTableModel(audiences));
    }
}
