package com.example.simplerequest.mvi.viewstate

sealed class KeyboardState {
    object isHidden : KeyboardState()
    object isShown : KeyboardState()
}