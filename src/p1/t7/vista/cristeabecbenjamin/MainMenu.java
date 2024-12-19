package p1.t7.vista.cristeabecbenjamin;

import java.awt.*;
import javax.swing.*;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Usuari;

public class MainMenu extends JFrame {
    private IClubOracleBD gBD;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel exportPanel;
    private UsuariPanel usuariPanel;
    private Usuari currentUser;
    
    public MainMenu(IClubOracleBD gBD, Usuari user) {
        this.gBD = gBD;
        this.currentUser = user;
        setTitle("Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        //setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Panel.background", Color.LIGHT_GRAY);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        JPanel welcomePanel = crearWelcomePanel();
        JPanel equipsPanel = new EquipsWindow(gBD);
        JPanel temporadesPanel = new TempWindow(gBD);
        JPanel jugadorsPanel = new JugadorsPanel(gBD);
        exportPanel = new ExportPanel(gBD);
        usuariPanel = new UsuariPanel(gBD);
        usuariPanel.setCurrentUser(currentUser);
        
        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(equipsPanel, "equips");
        contentPanel.add(temporadesPanel, "temporades");
        contentPanel.add(jugadorsPanel, "jugadors");
        contentPanel.add(exportPanel, "export");
        contentPanel.add(usuariPanel, "usuari");
        
        setLayout(new BorderLayout());

        sidePanel();

        setVisible(true);
    }

    private JPanel crearWelcomePanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        JLabel lblWelcome = new JLabel("Benvingut/da al gestor de clubs esportius del gran Benjamin Cristea!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);
        return contentPanel;
    }
    
    private void sidePanel() {
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new GridLayout(0, 1, 5, 5));
        sideMenu.setBackground(new Color(54, 57, 63));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnEquips = crearBoton("Equips");
        JButton btnJugadors = crearBoton("Jugadors");
        JButton btnTemp = crearBoton("Temporada");
        JButton btnUsuari = crearBoton("Usuari");
        JButton btnExport = crearBoton("Exportar");
        JButton btnSortir = crearBoton("Sortir");

        sideMenu.add(btnEquips);
        sideMenu.add(btnJugadors);
        sideMenu.add(btnTemp);
        sideMenu.add(btnUsuari);
        sideMenu.add(btnExport);
        sideMenu.add(new JLabel());
        sideMenu.add(btnSortir);

        
        add(sideMenu, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnEquips.addActionListener(e -> cardLayout.show(contentPanel, "equips"));
        btnJugadors.addActionListener(e -> cardLayout.show(contentPanel, "jugadors"));
        btnTemp.addActionListener(e -> cardLayout.show(contentPanel, "temporades"));
        btnUsuari.addActionListener(e -> cardLayout.show(contentPanel, "usuari"));
        btnExport.addActionListener(e -> cardLayout.show(contentPanel, "export"));
        btnSortir.addActionListener(e -> System.exit(0));
    }

    private JButton crearBoton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(47, 49, 54));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    private void mostrarContenido(JPanel contentPanel, String mensaje) {
        contentPanel.removeAll();
        JLabel lbl = new JLabel(mensaje, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        contentPanel.add(lbl, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    
}
