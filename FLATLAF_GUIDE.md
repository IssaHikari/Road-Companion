# 🎨 Guide d'utilisation de FlatLaf dans AissaGo

## 📋 Vue d'ensemble

Ce document explique comment FlatLaf est intégré dans l'application AissaGo et comment l'utiliser efficacement.

## ✅ État actuel de l'intégration

### 1. **Bibliothèque installée**
- **Fichier**: `lib/flatlaf-3.7.jar`
- **Version**: 3.7
- **Configuration**: Correctement référencée dans `nbproject/project.properties`

### 2. **Point d'entrée principal**
FlatLaf est initialisé dans `src/aissagoapp/AissaGoApp.java` :

```java
public static void main(String[] args) {
    try {
        // Activer le thème clair
        FlatLightLaf.setup();
        
        // Personnalisations
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    } catch (Exception ex) {
        System.err.println("Erreur FlatLaf: " + ex.getMessage());
    }
    
    SwingUtilities.invokeLater(() -> {
        new LoginFrame().setVisible(true);
    });
}
```

### 3. **Fichiers utilisant FlatLaf**
- ✅ `AissaGoApp.java` - Point d'entrée (initialisation globale)
- ✅ `LoginFrame.java` - Écran de connexion
- ✅ `MainFrame.java` - Interface principale
- ✅ `RegistrationFrame.java` - Inscription
- ✅ `PaymentDialog.java` - Dialogue de paiement
- ✅ Tous les autres composants héritent automatiquement du style

## 🎨 Thèmes disponibles

### Thème Clair (Actuel)
```java
FlatLightLaf.setup();
```

### Thème Sombre
Pour activer le mode sombre, modifier dans `AissaGoApp.java` :
```java
// Remplacer:
FlatLightLaf.setup();

// Par:
FlatDarkLaf.setup();
```

### Autres thèmes disponibles
FlatLaf propose également :
- `FlatIntelliJLaf` - Style IntelliJ IDEA
- `FlatDarculaLaf` - Style Darcula (sombre)

## 🔧 Personnalisations disponibles

### Coins arrondis
```java
UIManager.put("Button.arc", 10);           // Boutons
UIManager.put("Component.arc", 10);        // Composants généraux
UIManager.put("TextComponent.arc", 10);    // Champs de texte
```

### Couleurs personnalisées
```java
UIManager.put("Button.background", new Color(63, 81, 181));
UIManager.put("Button.foreground", Color.WHITE);
```

### Marges et espacements
```java
UIManager.put("Button.margin", new Insets(8, 16, 8, 16));
```

## 📝 Bonnes pratiques

### ✅ À FAIRE

1. **Initialiser FlatLaf avant toute interface**
   ```java
   FlatLightLaf.setup();
   SwingUtilities.invokeLater(() -> {
       // Créer vos fenêtres ici
   });
   ```

2. **Utiliser SwingUtilities.invokeLater()**
   - Toujours créer l'interface dans le thread EDT

3. **Personnaliser via UIManager**
   - Utiliser `UIManager.put()` pour les personnalisations globales

4. **Tester les deux thèmes**
   - Vérifier que votre interface fonctionne en mode clair ET sombre

### ❌ À ÉVITER

1. **Ne pas initialiser FlatLaf plusieurs fois**
   - Une seule initialisation au démarrage suffit

2. **Ne pas mélanger les Look and Feel**
   - Ne pas appeler `UIManager.setLookAndFeel()` après FlatLaf

3. **Éviter les couleurs codées en dur**
   - Utiliser les couleurs système de FlatLaf quand possible

## 🎯 Exemples d'utilisation

### Créer un bouton stylé
```java
JButton button = new JButton("Connexion");
// FlatLaf applique automatiquement le style moderne
```

### Créer un champ de texte
```java
JTextField field = new JTextField();
// Coins arrondis et style moderne automatiques
```

### Créer un panneau avec ombre
```java
JPanel panel = new JPanel();
panel.putClientProperty("FlatLaf.style", "arc: 20");
```

## 🔍 Propriétés FlatLaf utiles

### Pour les boutons
```java
button.putClientProperty("JButton.buttonType", "roundRect");
```

### Pour les panneaux
```java
panel.putClientProperty("FlatLaf.style", "arc: 15; background: #f5f5f5");
```

### Pour les fenêtres
```java
frame.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(63, 81, 181));
```

## 📚 Ressources supplémentaires

- **Documentation officielle**: https://www.formdev.com/flatlaf/
- **GitHub**: https://github.com/JFormDesigner/FlatLaf
- **Exemples**: https://www.formdev.com/flatlaf/themes/

## 🐛 Dépannage

### FlatLaf ne s'applique pas
1. Vérifier que `FlatLightLaf.setup()` est appelé AVANT toute création d'interface
2. Vérifier que le JAR est bien dans le classpath
3. Redémarrer l'application

### Composants avec style incorrect
1. Utiliser `SwingUtilities.updateComponentTreeUI(component)` pour forcer la mise à jour
2. Vérifier qu'aucun style personnalisé n'écrase FlatLaf

### Erreurs de compilation
1. Vérifier que `flatlaf-3.7.jar` est dans `lib/`
2. Vérifier `project.properties` : `file.reference.flatlaf-3.7.jar=lib/flatlaf-3.7.jar`

## 🎨 Palette de couleurs recommandée

Pour maintenir la cohérence avec FlatLaf :

```java
// Couleurs principales de l'application
Color BRAND_COLOR = new Color(63, 81, 181);      // Indigo
Color BG_COLOR = new Color(245, 247, 250);       // Gris clair
Color PRIMARY_GREEN = new Color(0, 200, 83);     // Vert
Color SIDEBAR_START = new Color(50, 20, 200);    // Bleu profond
Color SIDEBAR_END = new Color(150, 30, 200);     // Violet
```

## ✨ Conclusion

FlatLaf est maintenant correctement intégré dans AissaGo et fournit une interface moderne et professionnelle. Tous les composants Swing bénéficient automatiquement du style FlatLaf sans modification supplémentaire nécessaire.
