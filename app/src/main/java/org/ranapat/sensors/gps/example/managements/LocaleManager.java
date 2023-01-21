package org.ranapat.sensors.gps.example.managements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import androidx.preference.PreferenceManager;

import org.ranapat.sensors.gps.example.Settings;

import java.util.Locale;

@lombok.Generated
public class LocaleManager {
    private static final String DELIMITER = "-";

    private String language;
    private String script;

    public LocaleManager() {
        language = null;
        script = null;
    }

    public Context setLocale(final Context context) {
        return setLocale(context, getLanguage(context), getScript(context));
    }

    public Context setLocale(final Context context, final String code) {
        final String[] parameters = normalizeLanguageCode(code);

        return setLocale(context, parameters[0], parameters[1]);
    }

    public Context setLocale(final Context context, final String language, final String script) {
        if (language != null && !language.isEmpty()) {
            persistLanguage(context, language);
            this.language = language;

            persistScript(context, script);
            this.script = script;

            return setLocale(
                    context,
                    script != null && !script.isEmpty() ?
                            new Locale.Builder().setLanguage(language).setScript(script).build()
                            : new Locale.Builder().setLanguage(language).build()
            );
        } else {
            return context;
        }
    }

    @SuppressWarnings("deprecation")
    private Context setLocale(final Context context, final Locale locale) {
        Locale.setDefault(locale);

        final Configuration config = new Configuration(
                context.getResources().getConfiguration()
        );
        config.setLocale(locale);

        context
                .getResources()
                .updateConfiguration(
                        config,
                        context
                                .getResources()
                                .getDisplayMetrics()
                );

        return context;
    }

    public String getLanguage() {
        return language != null ? language : Settings.defaultUserLanguage;
    }

    public String getLanguage(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        language = prefs.getString(Settings.languageKey, getLocale(context).getLanguage());

        return language;
    }

    public String getScript() {
        return script != null ? script : Settings.defaultUserScript;
    }

    public String getScript(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        script = prefs.getString(Settings.scriptKey, getLocale(context).getScript());

        return script;
    }

    @SuppressWarnings("deprecation")
    private Locale getLocale(final Context context) {
        final Configuration config = context.getResources().getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("deprecation")
    private void persistLanguage(final Context context, final String language) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (language == null || !language.equals(prefs.getString(Settings.languageKey, getLocale(context).getLanguage()))) {
            prefs.edit()
                    .putString(Settings.languageKey, language)
                    .commit();
        }
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("deprecation")
    private void persistScript(final Context context, final String script) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (script == null || !script.equals(prefs.getString(Settings.scriptKey, getLocale(context).getScript()))) {
            prefs.edit()
                    .putString(Settings.scriptKey, script)
                    .commit();
        }
    }

    private String[] normalizeLanguageCode(final String code) {
        final String[] result = new String[2];

        if (code != null && !code.isEmpty()) {
            final String[] parts = code.split(DELIMITER);
            result[0] = parts.length >= 1 ? parts[0].substring(0, 2) : null;
            result[1] = parts.length >= 2 ? parts[1] : null;
        }

        return result;
    }
}
