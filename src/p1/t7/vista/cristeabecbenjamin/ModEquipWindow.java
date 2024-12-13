package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Categoria;
import org.milaifontanals.club.Equip;
import org.milaifontanals.club.Temporada;
import org.milaifontanals.club.Tipus;

public class ModEquipWindow extends JPanel {
    private JTextField txtNom;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<Temporada> cmbTemporada;
    private ButtonGroup tipusGroup;
    private JPanel parentContainer; // Contenedor principal
    private String mainPanelName;  // Nombre del panel principal
    
    private Equip selectedEquip;

    private List<Temporada> temporadas;
    
    private Color originalBackgroundColor;

    public ModEquipWindow(IClubOracleBD gBD, JPanel parentContainer, String mainPanelName, Equip selectedEquip) {
        this.parentContainer = parentContainer;
        this.mainPanelName = mainPanelName;
        this.selectedEquip = selectedEquip;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre del equipo
        JLabel lblNom = new JLabel("Nom de l'equip:");
        txtNom = new JTextField(20);
        originalBackgroundColor = txtNom.getBackground();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(lblNom, gbc);

        gbc.gridx = 1;
        inputPanel.add(txtNom, gbc);

        // Radiobuttons para el enum Tipus
        JLabel lblTipus = new JLabel("Tipus de l'equip:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(lblTipus, gbc);

        JPanel tipusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipusGroup = new ButtonGroup();

        for (Tipus tipus : Tipus.values()) {
            JRadioButton radioButton = new JRadioButton(tipus.getDisplayName());
            if(selectedEquip != null)
                radioButton.setEnabled(false);
            tipusGroup.add(radioButton);
            tipusPanel.add(radioButton);
        }

        gbc.gridx = 1;
        inputPanel.add(tipusPanel, gbc);

        // ComboBox para categoría
        JLabel lblCategoria = new JLabel("Categoria:");
        cmbCategoria = new JComboBox<>();
        carregarCategorias(gBD);
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(lblCategoria, gbc);

        gbc.gridx = 1;
        inputPanel.add(cmbCategoria, gbc);

        // ComboBox para temporada
        JLabel lblTemporada = new JLabel("Temporada:");
        cmbTemporada = new JComboBox<>();
        carregarTemporadas(gBD);
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(lblTemporada, gbc);

        gbc.gridx = 1;
        inputPanel.add(cmbTemporada, gbc);
        
        
        

        add(inputPanel, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(e -> {
            guardarEquip(gBD); 
            
        });

        btnCancelar.addActionListener(e -> volverAlPanelPrincipal());
        
        //Load equip
        if (selectedEquip != null) {
            txtNom.setText(selectedEquip.getNom());

            Tipus tipusSeleccionado = selectedEquip.getTipus();
            for (Enumeration<AbstractButton> buttons = tipusGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.getText().equals(tipusSeleccionado.getDisplayName())) {
                    button.setSelected(true);
                    break;
                }
            }

            Categoria categoriaSeleccionada = selectedEquip.getCategoria();
            for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                Categoria cat = cmbCategoria.getItemAt(i);
                if (cat.getId() == categoriaSeleccionada.getId()) {
                    cmbCategoria.setSelectedIndex(i);
                    break;
                }
            }

            Temporada temporadaSeleccionada = selectedEquip.getTemporada();
            for (int i = 0; i < cmbTemporada.getItemCount(); i++) {
                Temporada temp = cmbTemporada.getItemAt(i);
                if (temp.getYear()== temporadaSeleccionada.getYear()) {
                    cmbTemporada.setSelectedIndex(i);
                    break;
                }
            }
            cmbTemporada.setEnabled(false);
            cmbCategoria.setEnabled(false);

        }

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(buttonPanel, BorderLayout.SOUTH);
        
        txtNom.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNom.setBackground(originalBackgroundColor);
            }
        });
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

    private void carregarTemporadas(IClubOracleBD gBD) {
        try {
            temporadas = gBD.obtenirLlistaTemporada();
            for (Temporada temporada : temporadas) {
                cmbTemporada.addItem(temporada);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar les temporades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarEquip(IClubOracleBD gBD) {
        String nom = txtNom.getText().trim();

        if (nom.isEmpty()) {
            txtNom.setBackground(new Color(255, 200, 200));
            JOptionPane.showMessageDialog(this, 
                "El nom de l'equip és obligatori", 
                "Error de validació", 
                JOptionPane.WARNING_MESSAGE);
            txtNom.requestFocus();
        }else{
            try{

                txtNom.setBackground(originalBackgroundColor);

                Tipus t = obtenerTipusSeleccionado();
                Categoria c = (Categoria) cmbCategoria.getSelectedItem();
                Temporada temp = (Temporada) cmbTemporada.getSelectedItem();


                //System.out.println(e);
                if(selectedEquip == null){
                    Equip e = new Equip(nom, t, c, temp);
                    gBD.afegirEquip(e);
                } else {
                    selectedEquip.setNom(nom);
                    gBD.modificarEquip(selectedEquip);
                }
                gBD.confirmarCanvis();

                JOptionPane.showMessageDialog(this, "Equip guardat correctament!", "Èxit", JOptionPane.INFORMATION_MESSAGE);
                volverAlPanelPrincipal();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar l'equip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                infoError(ex);
            }
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

}
