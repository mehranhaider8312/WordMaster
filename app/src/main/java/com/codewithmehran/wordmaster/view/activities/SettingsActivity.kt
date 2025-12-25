package com.codewithmehran.wordmaster.view.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.databinding.ActivitySettingsBinding
import com.codewithmehran.wordmaster.model.WordMasterApp
import com.google.android.gms.ads.AdSize

class SettingsActivity : BaseAdActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val sharedPreferences by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
        setupBannerAd()
    }

    private fun setupUi() {
        binding.toolbar.title = getString(R.string.title_settings)
        binding.toolbar.setNavigationIcon(R.drawable.arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            trackClick() // Track click for interstitial ad
            finish()
        }

        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)
        binding.switchLightMode.isChecked = !isDarkMode
        binding.switchLightMode.text = if (isDarkMode) getString(R.string.settings_light_mode).replace("Light", "Dark") else getString(R.string.settings_light_mode) 

        binding.switchLightMode.isChecked = !isDarkMode

        binding.switchLightMode.setOnCheckedChangeListener { _, isChecked ->
            trackClick() // Track click for interstitial ad
            val newIsDarkMode = !isChecked
            sharedPreferences.edit().putBoolean("is_dark_mode", newIsDarkMode).apply()
            
            val mode = if (newIsDarkMode) {
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            } else {
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            }
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Language
        binding.rowLanguage.setOnClickListener {
            trackClick()
            showLanguageDialog()
        }

        // About
        binding.rowAbout.setOnClickListener {
            trackClick()
            startActivity(android.content.Intent(this, AboutActivity::class.java))
        }
    }

    private fun setupBannerAd() {
        val app = application as WordMasterApp
        val bannerAd = app.createBannerAdView(this, AdSize.MEDIUM_RECTANGLE)
        binding.bannerAdContainer.removeAllViews()
        binding.bannerAdContainer.addView(bannerAd)
    }

    private fun showLanguageDialog() {
        val items = arrayOf(
            getString(R.string.lang_english),
            getString(R.string.lang_urdu),
            getString(R.string.lang_hindi),
            getString(R.string.lang_arabic)
        )
        
        val currentLang = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val checkedItem = when {
            currentLang.contains("ur") -> 1
            currentLang.contains("hi") -> 2
            currentLang.contains("ar") -> 3
            else -> 0
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_language_title)
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                val langTag = when (which) {
                    0 -> "en"
                    1 -> "ur"
                    2 -> "hi"
                    3 -> "ar"
                    else -> "en"
                }
                androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                    androidx.core.os.LocaleListCompat.forLanguageTags(langTag)
                )
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}


