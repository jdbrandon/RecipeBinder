package com.jeffbrandon.recipebinder.viewmodel

sealed class Result<T> {
    class Loading<T> : Result<T>()
    class Loaded<T>(val data: T) : Result<T>()
    class Error<T>(val error: Throwable) : Result<T>()
}
