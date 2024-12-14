package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Usuari;
import org.milaifontanals.club.GestorBDClub;
import javax.swing.border.TitledBorder;

public class UsuariPanel extends JPanel {
    private IClubOracleBD gBD;
    private Usuari currentUser;
    
    private JTextField txtLogin;
    private JTextField txtNom;
    private JPasswordField txtPassword;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnNou;
    
    public UsuariPanel(IClubOracleBD gBD) {
        this.gBD = gBD;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Panel principal con GridBagLayout para mejor control del espacio
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Panel para los campos de datos
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Dades d'Usuari",
            TitledBorder.LEFT,
            TitledBorder.TOP));
        
        // Crear componentes
        txtLogin = new JTextField(20);
        txtNom = new JTextField(20);
        txtPassword = new JPasswordField(20);
        
        // Login (no editable)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        dataPanel.add(new JLabel("Login:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        txtLogin.setEditable(false);
        dataPanel.add(txtLogin, gbc);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        dataPanel.add(new JLabel("Nom:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(txtNom, gbc);
        
        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        dataPanel.add(new JLabel("Contrasenya:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dataPanel.add(txtPassword, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");
        btnNou = new JButton("Nou Usuari");
        
        styleButton(btnGuardar, new Color(46, 139, 87));
        styleButton(btnEliminar, new Color(178, 34, 34));
        styleButton(btnNou, new Color(70, 130, 180));
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnNou);
        
        // Añadir paneles al panel principal
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(dataPanel, gbc);
        
        gbc.gridy = 1;
        gbc.weighty = 0.0;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Configurar acciones de los botones
        setupActions();
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    private void setupActions() {
        btnGuardar.addActionListener(e -> guardarUsuari());
        btnEliminar.addActionListener(e -> eliminarUsuari());
        btnNou.addActionListener(e -> prepararNouUsuari());
    }
    
    public void setCurrentUser(Usuari user) {
        this.currentUser = user;
        if (user != null) {
            txtLogin.setText(user.getLogin());
            txtNom.setText(user.getNom());
            txtPassword.setText(""); // Por seguridad no mostramos la contraseña
            btnEliminar.setEnabled(true);
        }
    }
    
    private void guardarUsuari() {
        // Para usuario nuevo, todos los campos son obligatorios
        if (currentUser == null) {
            if (txtLogin.getText().trim().isEmpty() || 
                txtNom.getText().trim().isEmpty() || 
                new String(txtPassword.getPassword()).trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this,
                    "Tots els camps són obligatoris per a un nou usuari",
                    "Error de validació",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // Para usuario existente, solo el nombre es obligatorio
            if (txtNom.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "El nom és obligatori",
                    "Error de validació",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        try {
            String login = txtLogin.getText();
            String nom = txtNom.getText();
            String password = new String(txtPassword.getPassword());
            System.out.println(password);
            
            if (currentUser == null) {
                // Nuevo usuario - requiere todos los campos
                Usuari nouUsuari = new Usuari(nom, password, login);
                gBD.afegirUsuari(nouUsuari);
                JOptionPane.showMessageDialog(this, "Usuari creat correctament", "Èxit", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Usuario existente - actualizar solo lo necesario
                currentUser.setNom(nom);
                // Actualizar contraseña solo si se ha introducido una nueva
                if (!password.trim().isEmpty()) {
                    currentUser.setPassword(password);
                }
                gBD.modificarUsuari(currentUser);
                JOptionPane.showMessageDialog(this, "Usuari modificat correctament", "Èxit", JOptionPane.INFORMATION_MESSAGE);
            }
            gBD.confirmarCanvis();
        } catch (GestorBDClub ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarUsuari() {
        if (currentUser == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Estàs segur que vols eliminar aquest usuari?",
            "Confirmar eliminació",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gBD.esborrarUsuari(currentUser.getLogin());
                //gBD.confirmarCanvis();
                JOptionPane.showMessageDialog(this, "Usuari eliminat correctament", "Èxit", JOptionPane.INFORMATION_MESSAGE);
                
                Container c = this;
                while (c != null && !(c instanceof MainMenu)) {
                    c = c.getParent();
                }
                
                if (c instanceof MainMenu) {
                    MainMenu mainMenu = (MainMenu) c;
                    mainMenu.dispose();
                    new LoginWindow(gBD).setVisible(true);
                }
            } catch (GestorBDClub ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void prepararNouUsuari() {
        currentUser = null;
        txtLogin.setEditable(true);
        txtLogin.setText("");
        txtNom.setText("");
        txtPassword.setText("");
        btnEliminar.setEnabled(false);
    }
    
    private boolean validarCamps() {
        if (txtLogin.getText().trim().isEmpty() || 
            txtNom.getText().trim().isEmpty() || 
            new String(txtPassword.getPassword()).trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Tots els camps són obligatoris",
                "Error de validació",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
