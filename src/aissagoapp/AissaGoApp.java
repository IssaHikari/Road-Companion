
package aissagoapp;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import view.LoginFrame;

/**
 * Point d'entrée principal de l'application AissaGo
 * Application de covoiturage avec interface moderne utilisant FlatLaf
 */
public class AissaGoApp {

    public static void main(String[] args) {
        // Configurer FlatLaf avant de créer toute interface graphique
        try {
            // Utiliser le thème clair par défaut
            FlatLightLaf.setup();

            // Pour utiliser le thème sombre, décommenter la ligne suivante:
            // FlatDarkLaf.setup();

            // Personnalisations optionnelles de FlatLaf
            UIManager.put("Button.arc", 10); // Coins arrondis pour les boutons
            UIManager.put("Component.arc", 10); // Coins arrondis pour les composants
            UIManager.put("TextComponent.arc", 10); // Coins arrondis pour les champs de texte

        } catch (Exception ex) {
            System.err.println("Erreur lors de l'initialisation de FlatLaf: " + ex.getMessage());
            // Continuer avec le Look and Feel par défaut si FlatLaf échoue
        }

        // Preload heavy assets in a background thread
        new Thread(() -> {
            try {
                util.IconManager.getIcon("trip_bg.jpg");
                util.IconManager.getIcon("login_bg.jpg");
                util.IconManager.getIcon("bagraoung-Register.jpg");
            } catch (Exception e) {
                // Ignore, just preloading
            }
        }).start();

        // Lancer l'interface graphique dans le thread EDT
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
