package com.sduduzog.slimlauncher

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.sduduzog.slimlauncher.ui.main.MainViewModel
import com.sduduzog.slimlauncher.utils.Publisher
import com.sduduzog.slimlauncher.utils.Subscriber


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, Publisher {

    private lateinit var settings: SharedPreferences
    private lateinit var viewModel: MainViewModel
    private lateinit var navigator: NavController
    private val subscribers: MutableSet<Subscriber> = mutableSetOf()

    override fun attatchSubscriber(s: Subscriber) {
        subscribers.add(s)
    }

    override fun detachSubscriber(s: Subscriber) {
        subscribers.remove(s)
    }

    override fun dispatchBack() {
        for (s in subscribers) if (s.onBack()) return
        completeBackAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        settings = getSharedPreferences(getString(R.string.prefs_settings), MODE_PRIVATE)
        settings.registerOnSharedPreferenceChangeListener(this)
        navigator = findNavController(this, R.id.nav_host_fragment)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshApps()
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        toggleStatusBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) toggleStatusBar()
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, s: String?) {
        if (s.equals(getString(R.string.prefs_settings_key_theme), true)) {
            recreate()
        }
        if (s.equals(getString(R.string.prefs_settings_key_toggle_status_bar), true)) {
            toggleStatusBar()
        }
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        settings = getSharedPreferences(getString(R.string.prefs_settings), MODE_PRIVATE)
        val active = settings.getInt(getString(R.string.prefs_settings_key_theme), 0)
        theme.applyStyle(resolveTheme(active), true)
        return theme
    }

    override fun onBackPressed() { dispatchBack() }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun toggleStatusBar() {
        val isHidden = settings.getBoolean(getString(R.string.prefs_settings_key_toggle_status_bar), false)
        if (isHidden) {
            hideSystemUI()
        } else {
            showSystemUI()
        }
    }

    companion object {

        fun resolveTheme(i: Int): Int {
            when (i) {
                1 -> {
                    return R.style.AppDarkTheme
                }
                2 -> {
                    return R.style.AppGreyTheme
                }
                3 -> {
                    return R.style.AppTealTheme
                }
                4 -> {
                    return R.style.AppCandyTheme
                }
                5 -> {
                    return R.style.AppPinkTheme
                }
            }
            return R.style.AppTheme
        }
    }

    private fun completeBackAction(){ super.onBackPressed() }
}
