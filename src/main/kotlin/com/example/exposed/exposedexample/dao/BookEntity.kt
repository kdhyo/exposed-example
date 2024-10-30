package com.example.exposed.exposedexample.dao

import org.jetbrains.exposed.dao.id.LongIdTable

object BookEntity : LongIdTable("books") {
    val title = varchar("title", 255).index()
    val authorId = reference("author_id", AuthorEntity)
    val publisherId = reference("publisher_id", PublisherEntity)
    val publishedYear = integer("published_year")
}
