package com.mycompany.lotame;

import com.mycompany.lotame.api.LotameAPI;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Lotame Demo
 *
 */
public class App extends JPanel implements LoginPanelDelegate, GridPanelDelegate, AppDelegate {
    
    public static LotameAPI api;
    
    private LoginPanel loginPanel;
    private GridPanel gridPanel;
    private LoadingPanel loadingPanel;
    
    private JPanel current;
    
    public App() {
        super(new GridLayout(1, 0));
        
        loginPanel = new LoginPanel(this);
        gridPanel = new GridPanel(this);
        loadingPanel = new LoadingPanel();

        showView(loginPanel);
    }

    public static void createAndShowGui() {
        api = new LotameAPI();
        
        // Create and set up the window.
        JFrame frame = new JFrame("Lotame Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        JPanel panel = new App();
        panel.setOpaque(true);
        frame.setContentPane(panel);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                App.createAndShowGui();
            }
        });
    }

    public void onSuccessfulLogin(String token) {
        api.setActiveToken(token);
        
        gridPanel.loadGrid();
        
        showView(gridPanel);
    }

    public void onLogout() {
        showView(loginPanel);
    }
    
    private void showView(JPanel panel) {
        if (current != null) {
            current.setVisible(false);
            this.remove(current);
        }
        
        current = panel;
        
        this.add(current);
        current.setVisible(true);
    }

    public void hideLoading() {
        current.setVisible(true);
        loadingPanel.setVisible(false);
    }

    public void showLoading() {
        current.setVisible(false);
        loadingPanel.setVisible(true);
    }
}
