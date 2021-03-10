package View;

import Controller.SystemController;

import javax.swing.*;
import java.awt.*;

public class SystemView extends JFrame {

    private LoginView loginView = new LoginView();
    private HomeView homeView = new HomeView();

    private Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

    public SystemView() {
        this.add(loginView.getLogInPanel());

        setSize(600,800);
        setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public void addActionListeners(SystemController controller) {
        loginView.addActionListeners(controller);
        homeView.addActionListeners(controller);
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public HomeView getHomeView() {
        return homeView;
    }

    public void goToHome() {
        this.remove(loginView.getLogInPanel());
        //this.setSize(screenDimensions.width,screenDimensions.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);
        this.add(homeView.getHomePanel());
        this.repaint();
    }
}
