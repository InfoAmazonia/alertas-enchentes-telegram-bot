package br.edu.ufcg.analytics.infoamazonia.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Localisation
 * @date 25/01/15
 */
public class LocalisationService {
    private static LocalisationService instance = null;
    private final HashMap<String, String> supportedLanguages = new HashMap<>();

//    private ResourceBundle english;
//    private ResourceBundle spanish;
    private ResourceBundle portuguesebr;

    private class CustomClassLoader extends ClassLoader {
        public CustomClassLoader(ClassLoader parent) {
            super(parent);

        }

        public InputStream getResourceAsStream(String name) {
            InputStream utf8in = getParent().getResourceAsStream(name);
            if (utf8in != null) {
                try {
                    byte[] utf8Bytes = new byte[utf8in.available()];
                    utf8in.read(utf8Bytes, 0, utf8Bytes.length);
                    byte[] iso8859Bytes = new String(utf8Bytes, "UTF-8").getBytes("ISO-8859-1");
                    return new ByteArrayInputStream(iso8859Bytes);
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    try {
                        utf8in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    /**
     * Singleton
     *
     * @return Instance of localisation service
     */
    public static LocalisationService getInstance() {
        if (instance == null) {
            synchronized (LocalisationService.class) {
                if (instance == null) {
                    instance = new LocalisationService();
                }
            }
        }
        return instance;
    }

    /**
     * Private constructor due to singleton
     */
    private LocalisationService() {
        CustomClassLoader loader = new CustomClassLoader(Thread.currentThread().getContextClassLoader());
//        english = ResourceBundle.getBundle("localisation.strings", new Locale("en", "US"), loader);
//        supportedLanguages.put("en", "English");
//        spanish = ResourceBundle.getBundle("localisation.strings", new Locale("es", "ES"), loader);
//        supportedLanguages.put("es", "Español");
        portuguesebr = ResourceBundle.getBundle("localisation.strings", new Locale("pt", "BR"), loader);
        supportedLanguages.put("pt_br", "Portuguese BR");
    }

    /**
     * Get a string in default language (en)
     *
     * @param key key of the resource to fetch
     * @return fetched string or error message otherwise
     */
    public String getString(String key) {
        String result;
        try {
            result = getString(key, "pt-BR");
        } catch (MissingResourceException e) {
            result = "String not found";
        }

        return result;
    }

    /**
     * Get a string in default language
     *
     * @param key key of the resource to fetch
     * @return fetched string or error message otherwise
     */
    public String getString(String key, String language) {
        String result;
        try {
            switch (language.toLowerCase()) {
//                case "en":
//                    result = english.getString(key);
//                    break;
//                case "es":
//                    result = spanish.getString(key);
//                    break;
                case "pt-BR":
                    result = portuguesebr.getString(key);
                    break;
                default:
                    result = portuguesebr.getString(key);
                    break;
            }
        } catch (MissingResourceException e) {
            result = portuguesebr.getString(key);
        }

        return result;
    }

    public HashMap<String, String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public String getLanguageCodeByName(String language) {
        return supportedLanguages.entrySet().stream().filter(x -> x.getValue().equals(language)).findFirst().get().getKey();
    }
}
