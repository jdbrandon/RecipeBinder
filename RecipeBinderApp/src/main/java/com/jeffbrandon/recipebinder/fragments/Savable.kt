package com.jeffbrandon.recipebinder.fragments

interface Savable {
    fun edit()
    suspend fun save()
}
