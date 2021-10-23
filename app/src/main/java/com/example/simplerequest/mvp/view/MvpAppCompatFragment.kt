package com.example.simplerequest.mvp.view

import com.arellomobile.mvp.MvpDelegate

import android.os.Bundle
import androidx.fragment.app.Fragment


open class MvpAppCompatFragment: Fragment() {
    private var mIsStateSaved = false
    private var mMvpDelegate: MvpDelegate<out MvpAppCompatFragment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMvpDelegate().onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mIsStateSaved = false
        getMvpDelegate().onAttach()
    }

    override fun onResume() {
        super.onResume()
        mIsStateSaved = false
        getMvpDelegate().onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mIsStateSaved = true
        getMvpDelegate().onSaveInstanceState(outState)
        getMvpDelegate().onDetach()
    }

    override fun onStop() {
        super.onStop()
        getMvpDelegate().onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getMvpDelegate().onDetach()
        getMvpDelegate().onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity?.isFinishing!!) {
            getMvpDelegate().onDestroy()
            return
        }

        if (mIsStateSaved) {
            mIsStateSaved = false
            return
        }

        var anyParentIsRemoving = false
        var parent: Fragment = requireParentFragment()
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving
            parent = parent.requireParentFragment()
        }
        if (isRemoving || anyParentIsRemoving) {
            getMvpDelegate().onDestroy()
        }
    }

    /**
     * @return The [MvpDelegate] being used by this Fragment.
     */
    private fun getMvpDelegate(): MvpDelegate<*> {
        if (mMvpDelegate == null) {
            mMvpDelegate = MvpDelegate(this)
        }
        return mMvpDelegate as MvpDelegate<out MvpAppCompatFragment>
    }
}