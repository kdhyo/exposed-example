package com.example.exposed.exposedexample.dao

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object AuthorTable : LongIdTable("authors") {
    val name = varchar("name", 255).index()
    val birthDate = date("birth_date")
}
