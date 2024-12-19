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
    private Usuari user;

    public LoginWindow(IClubOracleBD gBD) {
        this.gBD = gBD;
        setTitle("Iniciar Sessió");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

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

                try {
                    if (gBD.validarLogin(usuario, password)) {
                        user = gBD.getUsuari(usuario);
                        JOptionPane.showMessageDialog(LoginWindow.this, "Benvingut/da " + user.getNom() + "!", "Login Correcte", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); 
                        
                        MainMenu mm = new MainMenu(gBD, user);
                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this, "Usuari o contrasenya incorrectes!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (GestorBDClub ex) {
                    infoError(ex);
                    JOptionPane.showMessageDialog(LoginWindow.this, "Error Inesperat: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public Usuari getUser() {
        return user;
    }
    private static void infoError(Throwable aux) {
        do {
            if (aux.getMessage() != null) {
                System.out.println("\t" + aux.getMessage());
            }
            aux = aux.getCause();
        } while (aux != null);
    }
}
