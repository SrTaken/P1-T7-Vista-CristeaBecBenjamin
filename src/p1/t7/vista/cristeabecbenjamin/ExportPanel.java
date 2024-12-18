package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import org.milaifontanals.club.IClubOracleBD;
import javax.swing.border.TitledBorder;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.milaifontanals.club.Categoria;
import org.milaifontanals.club.Equip;
import org.milaifontanals.club.Temporada;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExportPanel extends JPanel {
    private IClubOracleBD gBD;
    private ButtonGroup radioGroup;
    private JRadioButton rbJugador;
    private JRadioButton rbEquip;
    private JRadioButton rbMembre;
    private Properties props;
    private JComboBox<Temporada> cmbTemporada;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<Equip> cmbEquip;
    private JComboBox<String> cmbFormat;
    
    public ExportPanel(IClubOracleBD gBD) {
        this.gBD = gBD;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(350, 400));
        mainPanel.setBorder(createTitledBorder("Exportar Dades"));

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

        Font radioFont = new Font("SansSerif", Font.PLAIN, 14);
        rbJugador.setFont(radioFont);
        rbEquip.setFont(radioFont);
        rbMembre.setFont(radioFont);

        rbJugador.setSelected(true); 

        radioGroup.add(rbJugador);
        radioGroup.add(rbEquip);
        radioGroup.add(rbMembre);

        radioPanel.add(rbJugador);
        radioPanel.add(rbEquip);
        radioPanel.add(rbMembre);

        JLabel lblDescription = new JLabel("Selecciona el format d'exportació de les dades");
        lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDescription.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton btnCSV = new JButton("Exportar a CSV");
        JButton btnXML = new JButton("Exportar a XML");
        JButton btnJasper = new JButton("Exportar JSR");

        buttonPanel.add(btnCSV);
        buttonPanel.add(btnXML);
        buttonPanel.add(btnJasper);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(lblDescription);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(radioPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, gbc);

        btnCSV.addActionListener(e -> exportToCSV(getSelectedType()));
        btnXML.addActionListener(e -> exportToXML(getSelectedType()));
        btnJasper.addActionListener(e -> showJasperDialog());
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
        return "jugador";
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
                        int option = JOptionPane.showConfirmDialog(this,
                            "Vols incloure els equips dels jugadors?",
                            "Exportar Jugadors",
                            JOptionPane.YES_NO_OPTION);
                            
                        if (option == JOptionPane.YES_OPTION) {
                            gBD.exportJugadorsWithEquipsToCSV(file);
                        } else {
                            gBD.exportJugadorsToCSV(file);
                        }
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
                        int option = JOptionPane.showConfirmDialog(this,
                            "Vols incloure els equips dels jugadors?",
                            "Exportar Jugadors",
                            JOptionPane.YES_NO_OPTION);
                            
                        if (option == JOptionPane.YES_OPTION) {
                            gBD.exportJugadorsWithEquipsToXML(file);
                        } else {
                            gBD.exportJugadorsToXML(file);
                        }
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

    private void showJasperDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Seleccionar Filtres", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Temporada:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbTemporada = new JComboBox<>();
        cmbTemporada.addItem(null);  
        try {
            List<Temporada> temporadas = gBD.obtenirLlistaTemporada();
            for (Temporada t : temporadas) {
                cmbTemporada.addItem(t);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Error al carregar temporades: " + ex.getMessage());
        }
        filterPanel.add(cmbTemporada, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        filterPanel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem(null);  
        try {
            List<Categoria> categorias = gBD.obtenirLlistaCategoria();
            for (Categoria c : categorias) {
                cmbCategoria.addItem(c);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Error al carregar categories: " + ex.getMessage());
        }
        filterPanel.add(cmbCategoria, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        filterPanel.add(new JLabel("Equip:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbEquip = new JComboBox<>();
        cmbEquip.addItem(null);  
        try {
            List<Equip> equips = gBD.obtenirLlistaEquip();
            for (Equip e : equips) {
                cmbEquip.addItem(e);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Error al carregar equips: " + ex.getMessage());
        }
        filterPanel.add(cmbEquip, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.0;
        filterPanel.add(new JLabel("Format:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbFormat = new JComboBox<>(new String[]{"PDF", "XLSX"});
        filterPanel.add(cmbFormat, gbc);

        dialog.add(filterPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExport = new JButton("Exportar");
        JButton btnCancel = new JButton("Cancel·lar");

        btnExport.addActionListener(evt -> {
            dialog.dispose();
            exportJSR();
        });
        
        btnCancel.addActionListener(evt -> dialog.dispose());

        buttonPanel.add(btnExport);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void exportJSR() {
        try {
            final int BUFFER_SIZE = 4096;
            
            Properties props = new Properties();
            props.load(new FileInputStream("clubDB.properties"));
            
            String baseUrl = props.getProperty("JRurl");
            String username = props.getProperty("JRuser");
            String password = props.getProperty("JRpassword");
            
            String format = cmbFormat.getSelectedItem().toString().toLowerCase();
            StringBuilder urlBuilder = new StringBuilder(baseUrl);
            urlBuilder.append("FitxaEquip.").append(format);
            
            boolean hasParams = false;
            if (cmbTemporada.getSelectedItem() != null) {
                urlBuilder.append(hasParams ? "&" : "?");
                urlBuilder.append("Temporada=").append(((Temporada)cmbTemporada.getSelectedItem()).getYear());
                hasParams = true;
            }
            
            if (cmbCategoria.getSelectedItem() != null) {
                urlBuilder.append(hasParams ? "&" : "?");
                urlBuilder.append("Categoria=").append(((Categoria)cmbCategoria.getSelectedItem()).getId());
                hasParams = true;
            }
            
            if (cmbEquip.getSelectedItem() != null) {
                urlBuilder.append(hasParams ? "&" : "?");
                urlBuilder.append("Equip=").append(((Equip)cmbEquip.getSelectedItem()).getId());
            }
            
            URL obj = new URL(urlBuilder.toString());
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            
            String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            con.setRequestProperty("Authorization", "Basic " + auth);
            
            String contentType;
            switch (format) {
                case "pdf":
                    contentType = "application/pdf";
                    break;
                case "html":
                    contentType = "text/html";
                    break;
                case "csv":
                    contentType = "text/csv";
                    break;
                case "rtf":
                    contentType = "application/rtf";
                    break;
                default:
                    contentType = "application/octet-stream";
            }
            
            con.setRequestProperty("Accept", contentType);
            
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = con.getHeaderField("Content-Disposition");
                
                StringBuilder fileNameBuilder = new StringBuilder("FitxaEquip");
                if (cmbTemporada.getSelectedItem() != null) {
                    fileNameBuilder.append("_").append(((Temporada)cmbTemporada.getSelectedItem()).getYear());
                }
                if (cmbCategoria.getSelectedItem() != null) {
                    fileNameBuilder.append("_").append(((Categoria)cmbCategoria.getSelectedItem()).getCategoria());
                }
                if (cmbEquip.getSelectedItem() != null) {
                    fileNameBuilder.append("_").append(((Equip)cmbEquip.getSelectedItem()).getNom());
                }
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                fileNameBuilder.append("_").append(sdf.format(new Date()));
                fileNameBuilder.append(".").append(format);
                fileName = fileNameBuilder.toString();
                
                String userHome = System.getProperty("user.home");
                File downloadsFolder = new File(userHome, "Downloads");
                File outputFile = new File(downloadsFolder, fileName);
                
                try (InputStream inputStream = con.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(outputFile);
                    } catch (IOException ex) {
                        System.out.println("No hay aplicaciones disponibles para abrir el archivo");
                    }
                }
                
                JOptionPane.showMessageDialog(this,
                    "Informe generat correctament a: " + outputFile.getAbsolutePath(),
                    "Èxit",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al generar l'informe. Codi de resposta: " + responseCode,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
            con.disconnect();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al generar l'informe: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
}
