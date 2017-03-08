package hu.vaadin.spring.util;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;
import hu.vaadin.spring.enumeration.Language;
import hu.vaadin.spring.ui.LoginUI;
import java.util.Locale;

/**
 *
 * @author Szecskó Zoltán <zoltan.szecsko@inbuss.hu>
 */
@SpringComponent
public class LangChooser {

    private static final Language[] LANGUAGES = Language.values();

    public ComboBox getLangChooser() {
        final ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionItemId("");

        for (final Language l : LANGUAGES) {
            final String lang = l.toString();
            comboBox.addItem(lang);
            comboBox.addListener(getValueChangeListener());
        }

        comboBox.select(VaadinSession.getCurrent().getLocale().getLanguage());

        return comboBox;
    }

    private Property.ValueChangeListener getValueChangeListener() {
        final Property.ValueChangeListener valueChangeListener = (final Property.ValueChangeEvent event) -> {
            final UI ui = UI.getCurrent();
            final String selectedLanguage = event.getProperty().getValue().toString();
            final Locale selectedLocale = new Locale(selectedLanguage);

            VaadinSession.getCurrent().setLocale(selectedLocale);

            if (ui != null && ui instanceof LoginUI) {
                ((LoginUI) ui).changeLoginLayoutLang(selectedLocale);
            }
        };

        return valueChangeListener;
    }
}
