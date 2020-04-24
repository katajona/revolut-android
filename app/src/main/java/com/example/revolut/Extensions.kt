package com.example.revolut

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

interface BindableAdapter<T> {
    fun setData(data: T)
}

@BindingAdapter("data")
fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: T?) {
    val adapter = recyclerView.adapter
    if (adapter is BindableAdapter<*> && data != null) {
        @Suppress("UNCHECKED_CAST")
        (adapter as BindableAdapter<T>).setData(data)
    }
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Boolean?) {
    view.visibility = if (value != null && value) View.VISIBLE else View.GONE
}

fun Fragment.setupToolBar(
    toolbar: Toolbar,
    title: String,
    enableHomeAsUp: Boolean = true
) {
    val activity = requireActivity() as? AppCompatActivity
    activity?.let {
        it.setSupportActionBar(toolbar)
        it.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(enableHomeAsUp)
        }
        toolbar.title = title
        toolbar.setNavigationOnClickListener { activity.onBackPressed() }
    }
}


fun Double.toFormattedString(): String {
    val formatter = NumberFormat.getInstance()
    formatter.maximumFractionDigits = 2
    return formatter.format(this)
}

fun String.toFormattedDouble(): Double {
    val formatter = NumberFormat.getInstance()
    formatter.maximumFractionDigits = 2
    return try {
        formatter.parse(this)?.toDouble() ?: 0.0
    } catch (e: ParseException) {
        0.0
    }
}