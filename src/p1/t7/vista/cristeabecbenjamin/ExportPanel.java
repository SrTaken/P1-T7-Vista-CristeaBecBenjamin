package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import org.milaifontanals.club.IClubOracleBD;
import javax.swing.border.TitledBorder;
import java.io.File;

public class ExportPanel extends JPanel {
    private IClubOracleBD gBD;
    private ButtonGroup radioGroup;
    private JRadioButton rbJugador;
    private JRadioButton rbEquip;
    private JRadioButton rbMembre;
    
    public ExportPanel(IClubOracleBD gBD) {
        this.gBD = gBD;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Panel principal centrado
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(400, 300));
        mainPanel.setBorder(createTitledBorder("Exportar Dades"));

        // Panel para los RadioButtons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        radioPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(),
            "Selecciona el tipus de dades",
            TitledBorder.CENTER,
            TitledBorder.TOP
        ));

        radioGroup = new ButtonGroup();
        rbJugador = new JRadioButton("Jugadors");
        rbEquip = new JRadioButton("Equips");
        rbMembre = new JRadioButton("Membres");

        // Estilizar los RadioButtons
        Font radioFont = new Font("SansSerif", Font.PLAIN, 14);
        rbJugador.setFont(radioFont);
        rbEquip.setFont(radioFont);
        rbMembre.setFont(radioFont);

        rbJugador.setSelected(true); // Opción por defecto

        radioGroup.add(rbJugador);
        radioGroup.add(rbEquip);
        radioGroup.add(rbMembre);

        radioPanel.add(rbJugador);
        radioPanel.add(rbEquip);
        radioPanel.add(rbMembre);

        // Añadir descripción
        JLabel lblDescription = new JLabel("Selecciona el format d'exportació de les dades");
        lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDescription.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Panel para los botones de exportación
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton btnCSV = createStyledButton("Exportar a CSV", new Color(46, 139, 87));
        JButton btnXML = createStyledButton("Exportar a XML", new Color(70, 130, 180));

        buttonPanel.add(btnCSV);
        buttonPanel.add(btnXML);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(lblDescription);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(radioPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, gbc);

        // Modificar los ActionListeners para incluir la selección
        btnCSV.addActionListener(e -> exportToCSV(getSelectedType()));
        btnXML.addActionListener(e -> exportToXML(getSelectedType()));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            title,
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(70, 70, 70)
        );
        border.setTitlePosition(TitledBorder.ABOVE_TOP);
        return border;
    }

    private String getSelectedType() {
        if (rbJugador.isSelected()) return "jugador";
        if (rbEquip.isSelected()) return "equip";
        if (rbMembre.isSelected()) return "membre";
        return "jugador"; // Por defecto
    }

    private void exportToCSV(String type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar CSV");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) {
                    file = new File(path + ".csv");
                }
                
                switch (type) {
                    case "jugador":
                        gBD.exportJugadorsToCSV(file);
                        break;
                    case "equip":
                        gBD.exportEquipsToCSV(file);
                        break;
                    case "membre":
                        gBD.exportMembresToCSV(file);
                        break;
                }
                
                JOptionPane.showMessageDialog(this,
                    "Dades exportades correctament a CSV",
                    "Èxit",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar a CSV: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToXML(String type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar XML");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files (*.xml)", "xml"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".xml")) {
                    file = new File(path + ".xml");
                }
                
                switch (type) {
                    case "jugador":
                        gBD.exportJugadorsToXML(file);
                        break;
                    case "equip":
                        gBD.exportEquipsToXML(file);
                        break;
                    case "membre":
                        gBD.exportMembresToXML(file);
                        break;
                }
                
                JOptionPane.showMessageDialog(this,
                    "Dades exportades correctament a XML",
                    "Èxit",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar a XML: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
}
