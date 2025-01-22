package com.unipi.mobdev.unipiaudiostories.classes

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.Locale

class LanguageHelper {

    companion object {
        fun setLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        }

        fun applySavedLocale(context: Context) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val languageCode = prefs.getString("language_preference", "en") ?: "en"
            setLocale(context, languageCode)
        }
    }
}