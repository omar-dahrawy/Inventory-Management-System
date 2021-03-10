package View;

import javax.swing.*;
import java.awt.event.ActionListener;

public class LoginView {

    private JPanel logInPanel;
    private JTextField usernameTextField;
    private JTextField passwordTextField;
    private JButton continueButton;

    public LoginView() {

    }

    public JPanel getLogInPanel() {
        return logInPanel;
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassword() {
        return passwordTextField.getText();
    }

    public JButton getContinueButton() {
        return continueButton;
    }

    public void addActionListeners(ActionListener controller) {
        continueButton.addActionListener(controller);
    }

}
