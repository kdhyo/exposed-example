package com.example.exposed.exposedexample.repository

import com.example.exposed.exposedexample.dao.AuthorTable
import com.example.exposed.exposedexample.dao.BookTable
import com.example.exposed.exposedexample.dao.PublisherTable
import com.example.exposed.exposedexample.support.addAndIfNotNull
import com.example.exposed.exposedexample.support.eqIfNotNull
import com.example.exposed.exposedexample.support.greaterEqIfNotNull
import com.example.exposed.exposedexample.support.lessEqIfNotNull
import com.example.exposed.exposedexample.support.likeIfNotNull
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.max
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MultiRepository {
    // 단순 Inner Join
    @Transactional(readOnly = true)
    fun findBooksByCountry(country: String): List<Pair<String, String>> {
        return (BookTable innerJoin AuthorTable innerJoin PublisherTable)
            .select(BookTable.title, AuthorTable.name)
            .where { PublisherTable.country eq country }
            .map { it[BookTable.title] to it[AuthorTable.name] }
    }

    // Inner Join + Group By
    @Transactional(readOnly = true)
    fun findBooksCountAndLatestYearByAuthor(): List<Triple<String, Long, Int?>> {
        return AuthorTable
            .join(BookTable, JoinType.INNER, AuthorTable.id, BookTable.authorId)
            .select(AuthorTable.name, BookTable.publishedYear.max(), BookTable.id.count())
            .groupBy(AuthorTable.id, AuthorTable.name)
            .map { Triple(it[AuthorTable.name], it[BookTable.id.count()], it[BookTable.publishedYear.max()]) }
    }

    // Inner Join + Group By + Order By + Limit
    @Transactional(readOnly = true)
    fun findPublisherWithMostBooks(): Pair<String, Long>? {
        return PublisherTable
            .join(BookTable, JoinType.INNER, PublisherTable.id, BookTable.publisherId)
            .select(PublisherTable.name, BookTable.id.count())
            .groupBy(PublisherTable.id)
            .orderBy(BookTable.id.count(), SortOrder.DESC)
            .limit(1)
            .map { it[PublisherTable.name] to it[BookTable.id.count()] }
            .singleOrNull()
    }

    // 동적쿼리
    @Transactional(readOnly = true)
    fun findBooksByDynamicFilters(
        country: String? = null,
        authorName: String? = null,
        startYear: Int? = null,
        endYear: Int? = null
    ): List<Triple<String, String, Int>> {
        return (BookTable innerJoin AuthorTable innerJoin PublisherTable)
            .select(BookTable.title, AuthorTable.name, BookTable.publishedYear)
            .addAndIfNotNull(PublisherTable.country eqIfNotNull country)
            .addAndIfNotNull(AuthorTable.name likeIfNotNull authorName)
            .addAndIfNotNull(BookTable.publishedYear greaterEqIfNotNull startYear)
            .addAndIfNotNull(BookTable.publishedYear lessEqIfNotNull endYear)
            .map { Triple(it[BookTable.title], it[AuthorTable.name], it[BookTable.publishedYear]) }
    }
}
