package com.valery.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {
    protected val disposableBag = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposableBag.clear()
    }

    protected fun Disposable.addToBag() {
        disposableBag.add(this)
    }
}