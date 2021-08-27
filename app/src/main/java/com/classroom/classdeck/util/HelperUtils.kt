package com.classroom.classdeck.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.thecode.aestheticdialogs.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


fun checkAboveOreo(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.O
}

fun checkAboveKitkat(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
}

fun checkAboveLollipop(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
}

fun isAboveR(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.R
}

fun isAboveP(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.P
}


fun Context.rateUs() {

    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("amzn://apps/android?p=$packageName")
            )
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=$packageName")
            )
        )
    }

}


fun getAfterDate(count: Int = 0): String {

    val currentCalendar = Calendar.getInstance()
    val myFormat = "MMM dd ,yyyy"  // you can use your own date format
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    currentCalendar.add(Calendar.DAY_OF_YEAR, count)
    return sdf.format(currentCalendar.time)
}

fun getTwoDecimalPlaces(amount: Double): String {


    val a = Math.round(amount).toInt()

    val df: DecimalFormat = DecimalFormat("#.##")
    val maltRequiredString: String = df.format(amount)
    return a.toString()

}

fun getDaysRemaining(endDate: String): Int {

    val cal = Calendar.getInstance()
    val myFormat = "MMM dd, yyyy"  // you can use your own date format
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    var daysDiff = ""


    var diffDays = 0
    try {

        val startDate = sdf.parse(getTodaysDate())
        val endDate = sdf.parse(endDate)
        val diff = endDate.time - startDate.time
        diffDays = (diff / (24 * 60 * 60 * 1000)).toInt()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return diffDays


}

fun getTodaysDate(): String {


    return SimpleDateFormat.getDateInstance().format(Date())
}



fun checkIfDateIsBeforeToday(date: String): Boolean {
    val myFormat = "MMM dd, yyyy"  // you can use your own date format
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    val date1 = sdf.parse(date)
    val date2 = sdf.parse(getTodaysDate())

    return date1.before(date2)

}

fun getCurrentTime(): String {

    val date = Date(System.currentTimeMillis())
    val dateFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    val time = dateFormat.format(date)
    return time

}


fun getTimeDiff(time: String): ArrayList<Int> {
    val ti = time.split(" ")
    val f = ti[0] + ":00 " + ti[1]
    val format = SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH)
    val date1 = format.parse(f)
    val date = Date(System.currentTimeMillis())
    val time = format.format(date)
    val date2 = format.parse(time)
    val mills = date1.time - date2.time
    val hours = (mills / (1000 * 60 * 60)).toInt()
    val mins = (mills / (1000 * 60)).toInt() % 60
    var sec = (mills / (1000)).toInt() % 60


    val diff = "$hours:$mins:$sec"

    if (sec < 0)
        sec = 0

    val diffList: ArrayList<Int> = arrayListOf(hours, mins, sec)
    return diffList

}

fun getDateTimeDiff(time: String): ArrayList<Int> {
    val ti = time.split(" ")
    val f = ti[0] + " " + ti[1] + " " + ti[2] + " " + ti[3] + ":00 " + ti[4]



    val format = SimpleDateFormat("MMM dd, yyyy hh:mm:ss aa", Locale.ENGLISH)
    val date1 = format.parse(f)
    val date = Date(System.currentTimeMillis())
    val time = format.format(date)
    val date2 = format.parse(time)
    val mills = date1.time - date2.time

    val days = (mills / (1000 * 60 * 60 * 24)).toInt()
    val hours = (mills / (1000 * 60 * 60)).toInt() % 24
    val mins = (mills / (1000 * 60)).toInt() % 60
    var sec = (mills / (1000)).toInt() % 60


    val diff = "$days:$hours:$mins:$sec"

    if (sec < 0)
        sec = 0

    val diffList: ArrayList<Int> = arrayListOf(days, hours, mins, sec)
    return diffList

}


fun getCurrentTimeSec(): String {
    val format = SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH)
    val date = Date(System.currentTimeMillis())
    val time = format.format(date)
    return time
}

fun checkTimeLessThanCurrentTime(time: String?): Boolean {


    val format = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    // time of game
    val date1 = format.parse(time).time
    // current time
    val date2 = format.parse(getCurrentTime()).time


    return (date2 > date1)

}

fun checkTimeLessThanCurrenTimeIncludingDays(dateTime: String?): Boolean {
    val format = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.ENGLISH)
    // time of game
    val date1 = format.parse(dateTime).time

    // current time
    val date2 = System.currentTimeMillis()


    return (date2 > date1)
}

fun Activity.showDialoge(title: String, message: String, isCancelable: Boolean) {


    AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.SUCCESS)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setDarkMode(false)
        .setGravity(Gravity.CENTER)
        .setAnimation(DialogAnimation.SHRINK)
        .setOnClickListener(object : OnDialogClickListener {
            override fun onClick(dialog: AestheticDialog.Builder) {
                dialog.dismiss()
            }
        })
        .show()


}

fun Activity.showCustomDialog(
    title: String,
    message: String,
    isCancelable: Boolean,
    dialogStyle: DialogStyle,
    dialogType: DialogType,
    dialogAnimation: DialogAnimation
) {


    AestheticDialog.Builder(this, dialogStyle, dialogType)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setDarkMode(false)
        .setGravity(Gravity.CENTER)
        .setAnimation(dialogAnimation)
        .setOnClickListener(object : OnDialogClickListener {
            override fun onClick(dialog: AestheticDialog.Builder) {
                dialog.dismiss()
            }
        })
        .show()

}


fun Activity.showNonCancelableDialoge(title: String, message: String, isCancelable: Boolean) {


    AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.ERROR)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setDarkMode(false)
        .setGravity(Gravity.CENTER)
        .setAnimation(DialogAnimation.WINDMILL)
        .show()

}



fun ImageView.setTint(@ColorRes colorRes: Int) {
    //   ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))

    this.setColorFilter(ContextCompat.getColor(context, colorRes));

}
