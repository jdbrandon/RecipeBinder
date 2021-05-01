package com.jeffbrandon.recipebinder.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE RecipeData ADD COLUMN servings INTEGER NOT NULL DEFAULT 1")
    }
}
