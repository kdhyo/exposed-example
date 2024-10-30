package com.example.exposed.exposedexample.dao.entity

import com.example.exposed.exposedexample.dao.BookTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BookEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<BookEntity>(BookTable)

    var title by BookTable.title
    var author by AuthorEntity referencedOn BookTable.authorId
    var publisher by PublisherEntity referencedOn BookTable.publisherId
    var publishedYear by BookTable.publishedYear
}
