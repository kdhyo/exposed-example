package com.example.exposed.exposedexample

import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnTransformer
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.time
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MealTimeNullTransformerTest {

    private val transformer = MealTimeNullTransformer()

    @Test
    fun `unwrap should convert Meal to LocalTime correctly`() {
        assertEquals(LocalTime(8, 0), transformer.unwrap(Meal.BREAKFAST))
        assertEquals(LocalTime(12, 0), transformer.unwrap(Meal.LUNCH))
        assertEquals(LocalTime(18, 0), transformer.unwrap(Meal.DINNER))
        assertEquals(null, transformer.unwrap(null))
    }

    @Test
    fun `wrap should convert LocalTime to Meal correctly`() {
        assertEquals(Meal.BREAKFAST, transformer.wrap(LocalTime(8, 0)))
        assertEquals(Meal.BREAKFAST, transformer.wrap(LocalTime(9, 59)))
        assertEquals(Meal.LUNCH, transformer.wrap(LocalTime(12, 0)))
        assertEquals(Meal.LUNCH, transformer.wrap(LocalTime(14, 59)))
        assertEquals(Meal.DINNER, transformer.wrap(LocalTime(18, 0)))
        assertEquals(Meal.DINNER, transformer.wrap(LocalTime(20, 0)))
        assertEquals(null, transformer.wrap(null))
    }
}

class MealTimeNullTransformer : ColumnTransformer<LocalTime?, Meal?> {
    override fun wrap(value: LocalTime?): Meal? = when {
        value == null -> null
        value.hour < 10 -> Meal.BREAKFAST
        value.hour < 15 -> Meal.LUNCH
        else -> Meal.DINNER
    }

    override fun unwrap(value: Meal?): LocalTime? = when (value) {
        Meal.BREAKFAST -> LocalTime(8, 0)
        Meal.LUNCH -> LocalTime(12, 0)
        Meal.DINNER -> LocalTime(18, 0)
        else -> null
    }
}

object Meals : Table() {
    val mealTime: Column<Meal?> = time("meal_time").nullable().transform(MealTimeNullTransformer())
}
enum class Meal {
    BREAKFAST,
    LUNCH,
    DINNER
}
//
//object Meals : Table() {
//    val mealTime: Column<Meal?> = time("meal_time").nullable().transform(MealTimeNullTransformer())
//}
//
//class MealTimeNullTransformer : ColumnTransformer<LocalTime?, Meal?> {
//    override fun unwrap(value: Meal?): LocalTime? = when (value) {
//        Meal.BREAKFAST -> LocalTime(8, 0)
//        Meal.LUNCH -> LocalTime(12, 0)
//        Meal.DINNER -> LocalTime(18, 0)
//        else -> null
//    }
//
//    override fun wrap(value: LocalTime?): Meal? = when {
//        value == null -> null
//        value.hour < 10 -> Meal.BREAKFAST
//        value.hour < 15 -> Meal.LUNCH
//        else -> Meal.DINNER
//    }
//}
