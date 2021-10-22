package com.example.simplerequest.view

import com.arellomobile.mvp.MvpDelegate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


open class MvpAppCompatActivity : AppCompatActivity() {
    private var mMvpDelegate: MvpDelegate<out MvpAppCompatActivity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        getMvpDelegate().onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        getMvpDelegate().onAttach()
    }

    override fun onResume() {
        super.onResume()
        getMvpDelegate().onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getMvpDelegate().onSaveInstanceState(outState)
        getMvpDelegate().onDetach()
    }

    override fun onStop() {
        super.onStop()
        getMvpDelegate().onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        getMvpDelegate().onDestroyView()
        if (isFinishing) {
            getMvpDelegate().onDestroy()
        }
    }

    private fun getMvpDelegate(): MvpDelegate<*> {
        if (mMvpDelegate == null) {
            mMvpDelegate = MvpDelegate(this)
        }
        return mMvpDelegate as MvpDelegate<out MvpAppCompatActivity>
    }
}