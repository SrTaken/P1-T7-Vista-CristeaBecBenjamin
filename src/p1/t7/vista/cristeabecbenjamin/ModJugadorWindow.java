package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Sexe;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Enumeration;
import org.milaifontanals.club.Jugador;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Calendar;

/**
 *
 * @author isard
 */
public class ModJugadorWindow extends JPanel {
    private JTextField txtIdLegal;
    private JTextField txtNom;
    private JTextField txtCognom;
    private ButtonGroup sexeGroup;
    private JSpinner dateSpinner;
    private JTextField txtIban;
    private JTextField txtAdresa;
    private JTextField txtFoto = new JTextField();
    private JTextField txtAnyRevisioMedica;
    private JTextField txtPoblacio;
    private JTextField txtCp;
    private JPanel parentContainer;
    private String mainPanelName;
    private Color originalBackgroundColor;
    private SimpleDateFormat dateFormat;
    private JLabel lblImagePreview;
    private final int IMAGE_PREVIEW_SIZE = 150;

    public ModJugadorWindow(IClubOracleBD gBD, JPanel parentContainer, String mainPanelName, Jugador selectedJugador) {
        this.parentContainer = parentContainer;
        this.mainPanelName = mainPanelName;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //datos personales
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Datos Personales"));
        
        addField(leftPanel, "ID Legal:", txtIdLegal = new JTextField(20), 0);
        addField(leftPanel, "Nom:", txtNom = new JTextField(20), 1);
        addField(leftPanel, "Cognom:", txtCognom = new JTextField(20), 2);
        
        //sexo
        JPanel sexePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sexeGroup = new ButtonGroup();
        JRadioButton rbHome = new JRadioButton("Home");
        JRadioButton rbDona = new JRadioButton("Dona");
        sexeGroup.add(rbHome);
        sexeGroup.add(rbDona);
        sexePanel.add(rbHome);
        sexePanel.add(rbDona);
        addComponent(leftPanel, "Sexe:", sexePanel, 3);
        
        //spinner data
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -100); 
        Date earliestDate = calendar.getTime();
        
        calendar = Calendar.getInstance();
        Date latestDate = calendar.getTime(); 
        
        calendar.add(Calendar.YEAR, -18); 
        Date initDate = calendar.getTime();

        SpinnerDateModel dateModel = new SpinnerDateModel(initDate,
                                                         earliestDate,
                                                         latestDate,
                                                         Calendar.DAY_OF_MONTH);
        
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        addComponent(leftPanel, "Data Naixement:", dateSpinner, 4);

        // direccion y otros datos
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Dirección y Datos Adicionales"));
        
        addField(rightPanel, "IBAN:", txtIban = new JTextField(20), 0);
        addField(rightPanel, "Adreça:", txtAdresa = new JTextField(20), 1);
        addField(rightPanel, "Població:", txtPoblacio = new JTextField(20), 2);
        addField(rightPanel, "Codi Postal:", txtCp = new JTextField(10), 3);
        addField(rightPanel, "Any Revisió Mèdica:", txtAnyRevisioMedica = new JTextField(10), 4);

        //img
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setBorder(BorderFactory.createTitledBorder("Foto"));
        
        lblImagePreview = new JLabel("No hi ha imatge");
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(IMAGE_PREVIEW_SIZE, IMAGE_PREVIEW_SIZE));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImagePreview.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnSelectImage = new JButton("Seleccionar Imatge");
        
        imagePanel.add(lblImagePreview, BorderLayout.CENTER);
        imagePanel.add(btnSelectImage, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        mainPanel.add(rightPanel, gbc);

        gbc.gridx = 2;
        mainPanel.add(imagePanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        if (selectedJugador != null) {
            txtIdLegal.setText(selectedJugador.getIdLegal());
            txtNom.setText(selectedJugador.getNom());
            txtCognom.setText(selectedJugador.getCognom());
            
            if (selectedJugador.getSexe() == Sexe.H) {
                rbHome.setSelected(true);
            } else {
                rbDona.setSelected(true);
            }
            
            if (selectedJugador.getData_naix() != null) {
                dateSpinner.setValue(selectedJugador.getData_naix());
            }
            txtIban.setText(selectedJugador.getIban());
            txtAdresa.setText(selectedJugador.getAdresa());
            txtPoblacio.setText(selectedJugador.getPoblacio());
            txtCp.setText(String.valueOf(selectedJugador.getCp()));
            txtFoto.setText(selectedJugador.getFoto());
            txtAnyRevisioMedica.setText(String.valueOf(selectedJugador.getAny_fi_revisio_medica()));
            if (selectedJugador.getFoto() != null && !selectedJugador.getFoto().isEmpty()) {
                loadImage();
            }
        }
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            if (validarCampos()) {
                guardarJugador(gBD, selectedJugador);
            }
        });

        btnCancelar.addActionListener(e -> volverAlPanelPrincipal());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        add(buttonPanel, BorderLayout.SOUTH);

        lblImagePreview.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectAndLoadImage();
            }
        });

        btnSelectImage.addActionListener(e -> selectAndLoadImage());
    }

    private boolean validarCampos() {
        boolean valido = true;
        
        if (txtIdLegal.getText().trim().isEmpty()) {
            txtIdLegal.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            txtIdLegal.setBackground(originalBackgroundColor);
        }

        if (txtNom.getText().trim().isEmpty()) {
            txtNom.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            txtNom.setBackground(originalBackgroundColor);
        }

        if (txtCognom.getText().trim().isEmpty()) {
            txtCognom.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            txtCognom.setBackground(originalBackgroundColor);
        }

        if (sexeGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, 
                "Has de seleccionar un sexe", 
                "Error de validació", 
                JOptionPane.WARNING_MESSAGE);
            valido = false;
        }

        Date selectedDate = (Date) dateSpinner.getValue();
        if (selectedDate == null) {
            dateSpinner.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            dateSpinner.setBackground(originalBackgroundColor);
        }

        if (txtIban.getText().trim().isEmpty()) {
            txtIban.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            txtIban.setBackground(originalBackgroundColor);
        }

        if (txtAdresa.getText().trim().isEmpty()) {
            txtAdresa.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            txtAdresa.setBackground(originalBackgroundColor);
        }

        if (txtPoblacio.getText().trim().isEmpty()) {
            txtPoblacio.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            txtPoblacio.setBackground(originalBackgroundColor);
        }

        try {
            if (txtCp.getText().trim().isEmpty()) {
                txtCp.setBackground(new Color(255, 200, 200));
                valido = false;
            } else {
                int cp = Integer.parseInt(txtCp.getText().trim());
                if (cp < 1000 || cp > 99999) {
                    throw new NumberFormatException("CP debe tener 5 dígitos");
                }
                txtCp.setBackground(originalBackgroundColor);
            }
        } catch (NumberFormatException e) {
            txtCp.setBackground(new Color(255, 200, 200));
            JOptionPane.showMessageDialog(this, 
                "El codi postal ha de ser un número de 5 dígits", 
                "Error de validació", 
                JOptionPane.WARNING_MESSAGE);
            valido = false;
        }

        if (txtFoto.getText().trim().isEmpty()) {
            txtFoto.setBackground(new Color(255, 200, 200));
            valido = false;
        } else {
            txtFoto.setBackground(originalBackgroundColor);
        }

        try {
            if (txtAnyRevisioMedica.getText().trim().isEmpty()) {
                txtAnyRevisioMedica.setBackground(new Color(255, 200, 200));
                valido = false;
            } else {
                Integer.parseInt(txtAnyRevisioMedica.getText().trim());
                txtAnyRevisioMedica.setBackground(originalBackgroundColor);
            }
        } catch (NumberFormatException e) {
            txtAnyRevisioMedica.setBackground(new Color(255, 200, 200));
            JOptionPane.showMessageDialog(this, 
                "Formato de año incorrecto. Usa formato numérico", 
                "Error de validació", 
                JOptionPane.WARNING_MESSAGE);
            valido = false;
        }

        if (!valido) {
            JOptionPane.showMessageDialog(this, 
                "Si us plau, omple tots els camps obligatoris", 
                "Error de validació", 
                JOptionPane.WARNING_MESSAGE);
        }

        return valido;
    }

    private void guardarJugador(IClubOracleBD gBD, Jugador jugadorExistente) {
        try {
            if (!validarCampos()) {
                return;
            }

            String nom = txtNom.getText().trim();
            String cognom = txtCognom.getText().trim();
            Sexe sexe = getSexeSeleccionado();
            Date dataNaix = (Date) dateSpinner.getValue();
            String idLegal = txtIdLegal.getText().trim();
            String iban = txtIban.getText().trim();
            String adresa = txtAdresa.getText().trim();
            String poblacio = txtPoblacio.getText().trim();
            int cp = Integer.parseInt(txtCp.getText().trim());
            String foto = txtFoto.getText().trim();
            int anyRevisio = Integer.parseInt(txtAnyRevisioMedica.getText().trim());

            if (jugadorExistente == null) {
                Jugador jugador = new Jugador(nom, cognom, sexe, dataNaix, idLegal, 
                    iban, adresa, poblacio, cp, foto, anyRevisio);
                gBD.afegirJugador(jugador);
            } else {
                jugadorExistente.setNom(nom);
                jugadorExistente.setCognom(cognom);
                jugadorExistente.setSexe(sexe);
                jugadorExistente.setData_naix(dataNaix);
                jugadorExistente.setIdLegal(idLegal);
                jugadorExistente.setIban(iban);
                jugadorExistente.setAdresa(adresa);
                jugadorExistente.setPoblacio(poblacio);
                jugadorExistente.setCp(cp);
                jugadorExistente.setFoto(foto);
                jugadorExistente.setAny_fi_revisio_medica(anyRevisio);
                gBD.modificarJugador(jugadorExistente);
            }

            gBD.confirmarCanvis();
            JOptionPane.showMessageDialog(this, 
                "Jugador guardat correctament!", 
                "Èxit", 
                JOptionPane.INFORMATION_MESSAGE);
            volverAlPanelPrincipal();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar el jugador: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            infoError(ex);
        }
    }

    private Sexe getSexeSeleccionado() {
        for (Enumeration<AbstractButton> buttons = sexeGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText().equals("Home") ? Sexe.H : Sexe.D;
            }
        }
        return null;
    }

    private void volverAlPanelPrincipal() {
        CardLayout layout = (CardLayout) parentContainer.getLayout();
        layout.show(parentContainer, mainPanelName);
    }
    
    private static void infoError(Throwable aux) {
        do {
            if (aux.getMessage() != null) {
                System.out.println("\t" + aux.getMessage());
            }
            aux = aux.getCause();
        } while (aux != null);
    }

    private void selectAndLoadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Imágenes", "jpg", "jpeg", "png", "gif"));
            
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                txtFoto.setText(selectedFile.getAbsolutePath());
                loadImage();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar la imagen: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadImage() {
        try {
            String imagePath = txtFoto.getText().trim();
            if (!imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    BufferedImage img = ImageIO.read(imageFile);
                    if (img != null) {
                        int originalWidth = img.getWidth();
                        int originalHeight = img.getHeight();
                        
                        double scale = Math.min(
                            (double) IMAGE_PREVIEW_SIZE / originalWidth,
                            (double) IMAGE_PREVIEW_SIZE / originalHeight
                        );
                        
                        int scaledWidth = (int) (originalWidth * scale);
                        int scaledHeight = (int) (originalHeight * scale);
                        
                        Image scaledImage = img.getScaledInstance(
                            scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        
                        lblImagePreview.setIcon(new ImageIcon(scaledImage));
                        lblImagePreview.setText("");
                    }
                }
            } else {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("No hi ha imatge");
            }
        } catch (Exception ex) {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("No hi ha imatge");
            System.err.println("Error al cargar la imagen: " + ex.getMessage());
        }
    }

    private void addField(JPanel panel, String label, JTextField field, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addComponent(JPanel panel, String label, JComponent component, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(component, gbc);
    }
} 