/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.lotame;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Chris
 */
public class LoadingPanel extends JPanel {
    public LoadingPanel() {
        super(new BorderLayout()); 
        this.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.2f));
        add(new JLabel("Loading..."), BorderLayout.CENTER);
    }
}
