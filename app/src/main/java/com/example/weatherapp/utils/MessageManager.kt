package com.example.weatherapp.utils

import android.app.Activity
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class MessageManager @Inject constructor(private val activity: Activity) {
    private val resources get() = activity.resources
    private val rootView get() = activity.findViewById<View>(android.R.id.content)

    fun showError(error: String? = null) =
        error?.let { showSnackBar(it) }

    fun showError(@StringRes errorRes: Int) =
        showSnackBar(resources.getString(errorRes))

    private fun showSnackBar(text: String) =
        Snackbar
            .make(rootView, text, Snackbar.LENGTH_LONG)
            .show()
}