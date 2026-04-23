package util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton to manage application language and resource bundles (i18n).
 */
public class LanguageManager {

    private static LanguageManager instance;
    private ResourceBundle resourceBundle;
    private Locale currentLocale;

    // Available Locales
    public static final Locale FRENCH = new Locale("fr");
    public static final Locale ENGLISH = new Locale("en");
    public static final Locale ARABIC = new Locale("ar");

    private LanguageManager() {
        // Default language
        loadResourceBundle(FRENCH);
    }

    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    /**
     * Change the application language.
     * 
     * @param locale Use LanguageManager.FRENCH, ENGLISH, or ARABIC
     */
    public void setLanguage(Locale locale) {
        loadResourceBundle(locale);
    }

    private void loadResourceBundle(Locale locale) {
        this.currentLocale = locale;
        // Looks for messages_xx.properties in "i18n" package
        this.resourceBundle = ResourceBundle.getBundle("i18n.messages", locale);
        Locale.setDefault(locale);
    }

    /**
     * Get a localized string by key.
     */
    public String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Helper to check if current language is Arabic (for Right-to-Left support
     * later)
     */
    public boolean isArabic() {
        return "ar".equals(currentLocale.getLanguage());
    }
}
