package com.example.exposed.exposedexample.dao

import org.jetbrains.exposed.dao.id.LongIdTable

object BookTable : LongIdTable("books") {
    val title = varchar("title", 255).index()
    val authorId = reference("author_id", AuthorTable)
    val publisherId = reference("publisher_id", PublisherTable)
    val publishedYear = integer("published_year")
}
