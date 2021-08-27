package com.classroom.classdeck.util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.red
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import com.classroom.classdeck.R
import com.google.android.material.snackbar.Snackbar
import com.marcoscg.dialogsheet.DialogSheet


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast_long(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { sb ->
        sb.setAction("Ok") {
            sb.dismiss()
        }
    }.show()
}

fun Context.transitionAnimationBundle(): Bundle {

    return ActivityOptions.makeCustomAnimation(
        this, android.R.anim.fade_in,
        android.R.anim.fade_out
    ).toBundle()

}


fun Activity.setFullScreen() {
    if (isAboveR()) {
        this.window.setDecorFitsSystemWindows(false)
        this.window.insetsController?.let {
            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }
}

fun Activity.setFullScreenWithBtmNav() {
    this.window.decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_IMMERSIVE
    )
}

fun Context.share(link: String?, type: String) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND

    shareIntent.type = "text/plain"
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        link
    )

    this.startActivity(Intent.createChooser(shareIntent, "Share to"))
}

fun Activity.setFullScreenForNotch() {

    this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        this.window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
    }

}


fun Activity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun Context.showInfoBottomSheet(title:String,msg: String) {

    DialogSheet(this)
        .setTitle(title)
        .setMessage(msg)
        .setColoredNavigationBar(true)
        .setTitleTextSize(20) // In SP
        .setCancelable(true)
        .setPositiveButton(android.R.string.ok) {
        }
        .setNegativeButton(android.R.string.cancel) {
        }
        .setRoundedCorners(true)
        .setBackgroundColor(Color.WHITE)
        .setButtonsColorRes(R.color.primary_color)
        .setNegativeButtonColorRes(R.color.red)
        .show()
}




