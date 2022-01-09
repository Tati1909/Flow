package com.example.flow

import kotlinx.coroutines.flow.Flow

interface StateFlow<out T> : Flow<T> {
    val value: T
}

/*interface MutableStateFlow<T> : StateFlow<T> {
    override var value: T
}*/