package ch.martinelli.vj.ui.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * All the texts are written in English.
 * This I18NProvider only translates messages when the Locale is not English.
 */
@Component
public class TranslationProvider implements I18NProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationProvider.class);

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(Locale.of("en"), Locale.of("de"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            LOGGER.warn("Translation called with empty key!");
            return "";
        }

        if (locale.getLanguage().equals("en")) {
            // This is the default language. The key is in English, so we don't need to translate it
            if (params.length > 0) {
                return new MessageFormat(key, locale).format(params);
            } else {
                return key;
            }
        }

        var bundle = ResourceBundle.getBundle("i18n.messages", locale);
        if (bundle == null) {
            return key;
        }

        try {
            var value = bundle.getString(key);
            if (params.length > 0) {
                return new MessageFormat(value, locale).format(params);
            } else {
                return value;
            }
        } catch (MissingResourceException e) {
            LOGGER.warn("Missing translation for key {}", key);
            return "!" + locale.getLanguage() + ": " + key;
        }
    }

}
