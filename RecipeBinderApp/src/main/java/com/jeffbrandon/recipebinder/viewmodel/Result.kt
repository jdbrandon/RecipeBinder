package com.jeffbrandon.recipebinder.viewmodel

sealed class Result {
    object Loading : Result()
    class Loaded<T>(val data: T) : Result()
    class Error(val error: Throwable) : Result()
}
