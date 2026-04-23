package util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconManager {

    // Cache to store loaded images in memory
    private static final java.util.Map<String, ImageIcon> imageCache = new java.util.HashMap<>();

    /**
     * Loads an icon from the classpath or filesystem.
     * Uses caching to improve performance.
     */
    public static ImageIcon getIcon(String name) {
        // Check cache first
        if (imageCache.containsKey(name)) {
            return imageCache.get(name);
        }

        ImageIcon icon = loadIcon(name);

        // Store in cache if found
        if (icon != null) {
            imageCache.put(name, icon);
        }

        return icon;
    }

    private static ImageIcon loadIcon(String name) {
        // 1. Try Classpath (Resources)
        String[] paths = {
                "/resources/icons/" + name,
                "/icons/" + name,
                "/images/" + name,
                name
        };

        for (String path : paths) {
            URL url = IconManager.class.getResource(path);
            if (url != null) {
                return new ImageIcon(url);
            }
        }

        // 2. Try Filesystem (Project Root)
        try {
            // Absolute Path Fix (Robustness for User Envirnoment)
            java.io.File f = new java.io.File("C:/Users/HP/Desktop/AissaGoApp/images/" + name);
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath());
            }

            // Check "images" folder in project root
            f = new java.io.File("images/" + name);
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath());
            }
            // Check "icons" folder in project root
            f = new java.io.File("icons/" + name);
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath());
            }
            // Try absolute path or just "name"
            f = new java.io.File(name);
            if (f.exists()) {
                return new ImageIcon(f.getAbsolutePath());
            }
        } catch (Exception e) {
            // Ignore
        }

        return null;
    }

    /**
     * Returns a resized icon.
     * Note: Resized icons are NOT cached by default to allow different sizes of the
     * same image.
     */
    public static ImageIcon getIcon(String name, int width, int height) {
        ImageIcon icon = getIcon(name);
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}
