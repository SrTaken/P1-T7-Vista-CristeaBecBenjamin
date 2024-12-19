package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;
import javax.swing.border.TitledBorder;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Categoria;
import org.milaifontanals.club.Equip;
import org.milaifontanals.club.Temporada;
import org.milaifontanals.club.Tipus;
import java.util.Calendar;

public class ModEquipWindow extends JPanel {
    private JTextField txtNom;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<Temporada> cmbTemporada;
    private ButtonGroup tipusGroup;
    private JPanel parentContainer; 
    private String mainPanelName;  
    
    private Equip selectedEquip;

    private List<Temporada> temporadas;

    private Color originalBackgroundColor;

    public ModEquipWindow(IClubOracleBD gBD, JPanel parentContainer, String mainPanelName, Equip selectedEquip) {
        this.parentContainer = parentContainer;
        this.mainPanelName = mainPanelName;
        this.selectedEquip = selectedEquip;
        
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel containerPanel = new JPanel(new BorderLayout(10, 10));
        containerPanel.setPreferredSize(new Dimension(500, 400));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            selectedEquip == null ? "Nou Equip" : "Modificar Equip",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblNom = new JLabel("Nom de l'equip:");
        txtNom = new JTextField(20);
        txtNom.setPreferredSize(new Dimension(150, 25));
        originalBackgroundColor = txtNom.getBackground();
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(lblNom, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        inputPanel.add(txtNom, gbc);

        JPanel tipusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        tipusPanel.setBorder(BorderFactory.createTitledBorder("Tipus de l'equip"));
        tipusGroup = new ButtonGroup();

        for (Tipus tipus : Tipus.values()) {
            JRadioButton radioButton = new JRadioButton(tipus.getDisplayName());
            radioButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
            tipusGroup.add(radioButton);
            tipusPanel.add(radioButton);
        }

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(tipusPanel, gbc);
        gbc.gridwidth = 1;

        JLabel lblCategoria = new JLabel("Categoria:");
        cmbCategoria = new JComboBox<>();
        cmbCategoria.setPreferredSize(new Dimension(150, 25));
        carregarCategorias(gBD);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(lblCategoria, gbc);
        
        gbc.gridx = 1;
        inputPanel.add(cmbCategoria, gbc);

        JLabel lblTemporada = new JLabel("Temporada:");
        cmbTemporada = new JComboBox<>();
        cmbTemporada.setPreferredSize(new Dimension(150, 25));
        carregarTemporadas(gBD);
        
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(lblTemporada, gbc);
        
        gbc.gridx = 1;
        inputPanel.add(cmbTemporada, gbc);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        containerPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnMembres = new JButton("Gestionar Membres");

        btnGuardar.setPreferredSize(new Dimension(90, 30));
        btnCancelar.setPreferredSize(new Dimension(90, 30));
        btnMembres.setPreferredSize(new Dimension(130, 30));
        btnMembres.setEnabled(selectedEquip != null); 

        btnGuardar.addActionListener(e -> {
            guardarEquip(gBD); 
            volverAlPanelPrincipal();
        });
        btnCancelar.addActionListener(e -> volverAlPanelPrincipal());
        btnMembres.addActionListener(e -> {
            MemberPanel memberPanel = new MemberPanel(gBD, selectedEquip);
            JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Gestió de Membres", true);
            dialog.setContentPane(memberPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnMembres);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(containerPanel);

        //Load equip
        if (selectedEquip != null) {
            txtNom.setText(selectedEquip.getNom());
            
            for (Enumeration<AbstractButton> buttons = tipusGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.getText().equals(selectedEquip.getTipus().getDisplayName())) {
                    button.setSelected(true);
                }
                button.setEnabled(false);  
            }

            Categoria categoriaSeleccionada = selectedEquip.getCategoria();
            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                Categoria cat = cmbCategoria.getItemAt(i);
                if (cat.getId() == categoriaSeleccionada.getId()) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }
            cmbCategoria.setEnabled(false);  

            Temporada temporadaSeleccionada = selectedEquip.getTemporada();
            for (int i = 0; i < cmbTemporada.getItemCount(); i++) {
                Temporada temp = cmbTemporada.getItemAt(i);
                if (temp.getYear() == temporadaSeleccionada.getYear()) {
                    cmbTemporada.setSelectedIndex(i);
                    break;
                }
            }
            cmbTemporada.setEnabled(false);  
        }

        txtNom.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNom.setBackground(originalBackgroundColor);
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(90, 30));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    private void carregarCategorias(IClubOracleBD gBD) {
        try {
            List<Categoria> categorias = gBD.obtenirLlistaCategoria();
            for (Categoria cat : categorias) {
                cmbCategoria.addItem(cat);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar les categories: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;  // Obtiene los ultimos 2 dígitos para que cuadra con los de la BD
    }

    private void carregarTemporadas(IClubOracleBD gBD) {
        try {
            temporadas = gBD.obtenirLlistaTemporada();
            int currentYear = getCurrentYear();
            for (Temporada temporada : temporadas) {
                cmbTemporada.addItem(temporada);
                if (temporada.getYear() == currentYear) {
                    cmbTemporada.setSelectedItem(temporada);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar les temporades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEquip(IClubOracleBD gBD) {
        try {
            String nom = txtNom.getText().trim();
            
            if (nom.isEmpty()) {
                txtNom.setBackground(new Color(255, 200, 200));
                JOptionPane.showMessageDialog(this, 
                    "El nom de l'equip és obligatori", 
                    "Error de validació", 
                    JOptionPane.WARNING_MESSAGE);
                txtNom.requestFocus();
                return;
            }
            
            txtNom.setBackground(originalBackgroundColor);
            
            Tipus t = obtenerTipusSeleccionado();
            Categoria c = (Categoria) cmbCategoria.getSelectedItem();
            Temporada temp = (Temporada) cmbTemporada.getSelectedItem();
            
            if(selectedEquip != null){
                selectedEquip.setNom(nom);
                gBD.modificarEquip(selectedEquip);
            }else{
                Equip e = new Equip(nom, t, c, temp);
                gBD.afegirEquip(e);
            }
            
            
            gBD.confirmarCanvis();
                    
            JOptionPane.showMessageDialog(this, "Equip guardat correctament!", "Èxit", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            volverAlPanelPrincipal();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar l'equip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            infoError(ex);
        }
    }
    private static void infoError(Throwable aux) {
        do {
            if (aux.getMessage() != null) {
                System.out.println("\t" + aux.getMessage());
            }
            aux = aux.getCause();
        } while (aux != null);
    }

    private void volverAlPanelPrincipal() {
        limpiarCampos();
        CardLayout layout = (CardLayout) parentContainer.getLayout();
        layout.show(parentContainer, mainPanelName);
    }
    
    private Tipus obtenerTipusSeleccionado() {
        for (Enumeration<AbstractButton> buttons = tipusGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return Tipus.fromDisplayName(button.getText());
            }
        }
        return null; 
    }

    private void limpiarCampos() {
        txtNom.setText("");
        tipusGroup.clearSelection();
        cmbCategoria.setSelectedIndex(0);
        
        int currentYear = getCurrentYear();
        for (int i = 0; i < cmbTemporada.getItemCount(); i++) {
            Temporada temp = cmbTemporada.getItemAt(i);
            if (temp.getYear() == currentYear) {
                cmbTemporada.setSelectedIndex(i);
                break;
            }
        }
        
        txtNom.setBackground(originalBackgroundColor);
        
        if (selectedEquip != null) {
            for (Enumeration<AbstractButton> buttons = tipusGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                button.setEnabled(true);
            }
            cmbCategoria.setEnabled(true);
            cmbTemporada.setEnabled(true);
        }
    }
}
