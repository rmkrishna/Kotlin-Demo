package com.muthu.salesmanager.util.ext

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

/**
 * Created by muthu on 28/1/18.
 */
inline fun DatabaseReference.childEventListener(init: MChildEventListener.() -> Unit) = addChildEventListener(MChildEventListener().apply(init))

class MChildEventListener : ChildEventListener {

    private var mOnChildRemoved: ((DataSnapshot?) -> Unit)? = null
    private var mOnChildChanged: ((DataSnapshot?, String?) -> Unit)? = null
    private var mOnChildMoved: ((DataSnapshot?, String?) -> Unit)? = null
    private var mOnChildAdded: ((DataSnapshot?, String?) -> Unit)? = null
    private var mOnCancelled: ((DatabaseError?) -> Unit)? = null

    override fun onChildRemoved(snapshot: DataSnapshot?) {
        mOnChildRemoved?.invoke(snapshot)
    }

    fun onChildRemoved(listener: ((DataSnapshot?) -> Unit)) {
        mOnChildRemoved = listener
    }

    override fun onChildChanged(snapshot: DataSnapshot?, previousChildName: String?) {
        mOnChildChanged?.invoke(snapshot, previousChildName)
    }

    fun onChildChanged(listener: ((DataSnapshot?, String?) -> Unit)) {
        mOnChildChanged = listener
    }

    override fun onChildMoved(snapshot: DataSnapshot?, previousChildName: String?) {
        mOnChildMoved?.invoke(snapshot, previousChildName)
    }

    fun onChildMoved(listener: ((DataSnapshot?, String?) -> Unit)) {
        mOnChildMoved = listener
    }

    override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
        mOnChildAdded?.invoke(snapshot, previousChildName)
    }

    fun onChildAdded(listener: ((DataSnapshot?, String?) -> Unit)) {
        mOnChildAdded = listener
    }

    override fun onCancelled(snapshot: DatabaseError?) {
        mOnCancelled?.invoke(snapshot)
    }

    fun onCancelled(listener: ((DatabaseError?) -> Unit)) {
        mOnCancelled = listener
    }
}