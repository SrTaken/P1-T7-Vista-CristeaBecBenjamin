/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p1.t7.vista.cristeabecbenjamin;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Usuari;

/**
 *
 * @author Benjamin Cristea
 */
public class IniciarPrograma {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println("Error al carregar THEME");
        }
                
        JFrame frame = new JFrame("Gestor de Connexió");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JLabel lblMensaje = new JLabel("Preparant el programa...", SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); 
        

        frame.add(lblMensaje, BorderLayout.CENTER);
        frame.add(progressBar, BorderLayout.SOUTH);
        
        frame.setVisible(true);

        if (args.length == 0) {
            lblMensaje.setText("<html><center>Error: Cal passar el nom de la classe<br />que dona la persistència com a argument</center></html>");
            progressBar.setIndeterminate(false); // Detener barra
            return;
        }

        String nomClassePersistencia = args[0];
        //variable mutable
        IClubOracleBD gBD[] = {null};
        

        //pijadas 
        new Thread(() -> {
            try {
                lblMensaje.setText("Intentant establir connexió amb la BD...");
                //Thread.sleep(2000); // Simular tiempo de conexión

                gBD[0] = (IClubOracleBD) Class.forName(nomClassePersistencia).newInstance();

                frame.dispose();

                LoginWindow loginWindow = new LoginWindow(gBD[0]);
                loginWindow.setVisible(true);
                Usuari user = loginWindow.getUser();
            } catch (ClassNotFoundException ex) {
                mostrarError(lblMensaje, progressBar, "Error: No s'ha trobat la classe " + nomClassePersistencia);
            } catch (InstantiationException | IllegalAccessException ex) {
                mostrarError(lblMensaje, progressBar, "Error: No s'ha pogut instanciar la classe " + nomClassePersistencia);
            } catch (Exception ex) {
                mostrarError(lblMensaje, progressBar, "Error inesperat: " + ex.getMessage());
                
            }
        }).start();
          
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (gBD[0] != null) {
                    try {
                        gBD[0].tancarCapa();
                        System.out.println("Connexió amb la BD tancada correctament.");
                    } catch (Exception e) {
                        System.err.println("Error en tancar la connexió: " + e.getMessage());
                    }
                }
            }));
    }

    private static void mostrarError(JLabel lblMensaje, JProgressBar progressBar, String mensajeError) {
        SwingUtilities.invokeLater(() -> {
            lblMensaje.setText("<html><center>" + mensajeError + "</center></html>");
            progressBar.setIndeterminate(false); 
        });
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
