/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p1.t7.vista.cristeabecbenjamin;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.milaifontanals.club.IClubOracleBD;

/**
 *
 * @author isard
 */
public class IniciarPrograma {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gestor de Connexió");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel lblMensaje = new JLabel("Preparant el programa...", SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); 

        frame.add(lblMensaje, BorderLayout.CENTER);
        frame.add(progressBar, BorderLayout.SOUTH);
        
        frame.setVisible(true);

        if (args.length == 0) {
            lblMensaje.setText("<html><center>Error: Cal passar el nom de la classe<br>que dona la persistència com a argument</center></html>");
            progressBar.setIndeterminate(false); // Detener barra
            return;
        }

        String nomClassePersistencia = args[0];
        //variable mutable
        final IClubOracleBD gBD[] = {null};

        //pijadas 
        new Thread(() -> {
            try {
                lblMensaje.setText("Intentant establir connexió amb la BD...");
                Thread.sleep(2000); // Simular tiempo de conexión

                gBD[0] = (IClubOracleBD) Class.forName(nomClassePersistencia).newInstance();

                SwingUtilities.invokeLater(() -> {
                    lblMensaje.setText("Connexió establerta amb èxit!");
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                });
                
                frame.dispose();

                new LoginWindow(gBD[0]).setVisible(true);
            } catch (ClassNotFoundException e) {
                mostrarError(lblMensaje, progressBar, "Error: No s'ha trobat la classe " + nomClassePersistencia);
            } catch (InstantiationException | IllegalAccessException e) {
                mostrarError(lblMensaje, progressBar, "Error: No s'ha pogut instanciar la classe " + nomClassePersistencia);
            } catch (Exception ex) {
                mostrarError(lblMensaje, progressBar, "Error inesperat: " + ex.getMessage());
            }
        }).start();
    }

    private static void mostrarError(JLabel lblMensaje, JProgressBar progressBar, String mensajeError) {
        SwingUtilities.invokeLater(() -> {
            lblMensaje.setText("<html><center>" + mensajeError + "</center></html>");
            progressBar.setIndeterminate(false); 
        });
    }
}
