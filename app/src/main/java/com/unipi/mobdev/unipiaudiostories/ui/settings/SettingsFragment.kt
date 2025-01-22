package com.unipi.mobdev.unipiaudiostories.ui.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.unipi.mobdev.unipiaudiostories.R
import com.unipi.mobdev.unipiaudiostories.classes.LanguageHelper

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<ListPreference>("language_preference")?.setOnPreferenceChangeListener { _, newValue ->
            val newLanguage = newValue as String
            LanguageHelper.setLocale(requireContext(), newLanguage)
            requireActivity().recreate() // Recreate the activity to apply the language
            true
        }
    }
}