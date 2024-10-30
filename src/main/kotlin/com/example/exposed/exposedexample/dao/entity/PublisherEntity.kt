package com.example.exposed.exposedexample.dao.entity

import com.example.exposed.exposedexample.dao.BookTable
import com.example.exposed.exposedexample.dao.PublisherTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID


class PublisherEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PublisherEntity>(PublisherTable)

    var name by PublisherTable.name
    var country by PublisherTable.country

    val books by BookEntity referrersOn BookTable.publisherId
}
