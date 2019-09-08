package com.rockspin.rxfluxandroid

import android.widget.EditText


fun EditText.rxSetText(newText: CharSequence) {
    if (this.text.toString() != newText.toString()) {
        this.setText(newText)
    }
}