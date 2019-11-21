package com.example.repetodo.Utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

interface ItemActionListener {
    fun onItemDelete(id: Long) {}
    fun onItemUpdate(id: Long, title: String) {}
    fun onItemCheckUpdate(id: Long, checked: Boolean) {}
    fun onInsertTemplate(id: Long) {}


    fun hideSoftKeyboard(activity: FragmentActivity, view: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
}