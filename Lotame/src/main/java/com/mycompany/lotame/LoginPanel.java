/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.lotame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Chris
 */
public class LoginPanel extends JPanel {
    private JTextField email;
    private JTextField password;
    
    public LoginPanel(final LoginPanelDelegate delegate) {
        super(new BorderLayout());
        
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");
        JButton login = new JButton("Login") {{
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        delegate.onSuccessfulLogin(App.api.getToken(email.getText(), password.getText()));
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }};
        
        email = new JTextField();
        password = new JPasswordField();
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(emailLabel);
        panel.add(email);
        panel.add(passwordLabel);
        panel.add(password);
        
        add(panel, BorderLayout.CENTER);
        add(login, BorderLayout.SOUTH);
        
        this.setPreferredSize(new Dimension(200, 75));
    }
}
