package com.capstone.viziaproject.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.capstone.viziaproject.R

class CustomEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = s.toString()
                if (id == R.id.editTextPassword && inputText.length < 8) {
                    error = context.getString(R.string.password_too_short)
                    setError("Password tidak boleh kurang dari 8 karakter", null)
                }else if(id == R.id.editTextPassword2 && inputText.length < 8) {
                    error = context.getString(R.string.password_too_short)
                    setError("Password tidak boleh kurang dari 8 karakter", null)
                }else if (id == R.id.editTextEmail && !android.util.Patterns.EMAIL_ADDRESS.matcher(inputText).matches()) {
                    error = context.getString(R.string.invalid_email)
                    setError("Format Email salah", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}