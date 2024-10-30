package com.example.exposed.exposedexample.dao

import org.jetbrains.exposed.dao.id.LongIdTable

object PublisherTable : LongIdTable("publishers") {
    val name = varchar("name", 255).index()
    val country = varchar("country", 100)
}
