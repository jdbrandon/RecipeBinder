package com.jeffbrandon.recipebinder.dagger

import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.data.Instruction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Cant use dagger to provide this implementation because of how Room type converters work.
 * So, we provide the implementation manually and then expose it to dagger for use in other
 * injected components
 */
@Module
@InstallIn(SingletonComponent::class)
class MoshiModule {
    companion object {
        private val instance by lazy {
            Moshi.Builder().build()
        }
        val ingredientConverter: JsonAdapter<List<Ingredient>> by lazy {
            instance.adapter(Types.newParameterizedType(List::class.java, Ingredient::class.java))
        }
        val instructionConverter: JsonAdapter<List<Instruction>> by lazy {
            instance.adapter(Types.newParameterizedType(List::class.java, Instruction::class.java))
        }
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = instance

    @Provides
    @Singleton
    fun provideIngredientListJsonAdapter() = ingredientConverter

    @Provides
    @Singleton
    fun provideInstructionListJsonAdapter() = instructionConverter
}
