package com.muthu.salesmanager.util.ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by muthu on 27/1/18.
 */
inline fun EditText.textWatcher(init: MTextWatcher.() -> Unit) = addTextChangedListener(MTextWatcher().apply(init))

class MTextWatcher : TextWatcher {

    private var mBeforeTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var mOnTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var mAfterTextChanged: ((Editable?) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            mBeforeTextChanged?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mOnTextChanged?.invoke(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {
        mAfterTextChanged?.invoke(s)
    }

    fun beforeTextChanged(listener: (CharSequence?, Int, Int, Int) -> Unit) {
        mBeforeTextChanged = listener
    }

    fun onTextChanged(listener: (CharSequence?, Int, Int, Int) -> Unit) {
        mOnTextChanged = listener
    }

    fun afterTextChanged(listener: (Editable?) -> Unit) {
        mAfterTextChanged = listener
    }

}