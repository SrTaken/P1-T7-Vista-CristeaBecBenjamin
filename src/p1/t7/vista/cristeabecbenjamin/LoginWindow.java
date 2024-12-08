/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p1.t7.vista.cristeabecbenjamin;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.milaifontanals.club.GestorBDClub;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Usuari;

/**
 *
 * @author isard
 */
public class LoginWindow extends JFrame {
    private IClubOracleBD gBD;
    public LoginWindow(IClubOracleBD gBD) {
        this.gBD = gBD;
        setTitle("Iniciar Sessió");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblUsuario = new JLabel("Usuari:");
        JTextField txtUsuario = new JTextField();

        JLabel lblPassword = new JLabel("Contrasenya:");
        JPasswordField txtPassword = new JPasswordField();

        JButton btnLogin = new JButton("Login");

        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(new JLabel()); 
        panel.add(btnLogin);

        add(panel);
    
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText();
                String password = Usuari.cifratePassword(new String(txtPassword.getPassword()));
                //System.out.println(password); 
                try {
                    if (gBD.validarLogin(usuario, password)) {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Benvingut/da " + usuario + "!", "Login Correcte", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Usuari o contrasenya incorrectes!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (GestorBDClub ex) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Error Inesperat", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
