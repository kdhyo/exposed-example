package com.example.exposed.exposedexample.dao

import org.jetbrains.exposed.dao.id.LongIdTable

object PublisherEntity : LongIdTable("publishers") {
    val name = varchar("name", 255).index()
    val country = varchar("country", 100)
}
