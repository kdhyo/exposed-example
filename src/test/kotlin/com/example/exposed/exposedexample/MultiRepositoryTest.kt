package com.example.exposed.exposedexample

import com.example.exposed.exposedexample.dao.AuthorTable
import com.example.exposed.exposedexample.dao.BookTable
import com.example.exposed.exposedexample.dao.PublisherTable
import com.example.exposed.exposedexample.dao.entity.AuthorEntity
import com.example.exposed.exposedexample.dao.entity.PublisherEntity
import com.example.exposed.exposedexample.repository.MultiRepository
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.sql.batchInsert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@Transactional
class MultiRepositoryTest {
    @Autowired
    lateinit var repository: MultiRepository

    @Test
    fun `특정 나라의 출판사가 발행한 책과 저자를 조회할 수 있다`() {
        val result = repository.findBooksByCountry("Country A")
        assertTrue(result.isNotEmpty())
        result.forEach { (bookTitle, authorName) ->
            println("Book: $bookTitle, Author: $authorName")
        }
    }

    @Test
    fun `저자별로 출판한 책의 개수와 최신 발행 연도를 조회할 수 있다`() {
        val result = repository.findBooksCountAndLatestYearByAuthor()
        assertEquals(10, result.size)
        result.forEach { (authorName, bookCount, latestYear) ->
            assertTrue(bookCount > 0)
            println("Author: $authorName, Book Count: $bookCount, Latest Year: $latestYear")
        }
    }

    @Test
    fun `가장 많은 책을 출판한 출판사의 이름과 출판한 책 수를 조회할 수 있다`() {
        val result = repository.findPublisherWithMostBooks()
        assertNotNull(result)
        assertEquals("Publisher 3", result!!.first)
        println("Publisher: ${result.first}, Count: ${result.second}")
    }

    @Test
    fun `동적 조건으로 나라별 출판사가 발행한 책과 저자 정보를 조회할 수 있다`() {
        val result = repository.findBooksByDynamicFilters(country = "Country A")
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        result.forEach { (bookTitle, authorName, publishedYear) ->
            println("Book: $bookTitle, Author: $authorName, Published Year: $publishedYear")
        }
    }

    @Test
    fun `동적 조건으로 특정 저자와 발행 연도 범위로 책을 조회할 수 있다`() {
        val result = repository.findBooksByDynamicFilters(authorName = "Author 3", startYear = 2003, endYear = 2015)
        assertTrue(result.isNotEmpty())
        result.forEach { (bookTitle, authorName, publishedYear) ->
            assertEquals("Author 3", authorName)
            assertTrue(publishedYear in 2003..2005)
            println("Book: $bookTitle, Author: $authorName, Published Year: $publishedYear")
        }
    }

    @Test
    fun `동적 조건 없이 전체 책 정보를 조회할 수 있다`() {
        val result = repository.findBooksByDynamicFilters()
        assertTrue(result.size >= 10)
    }

    @BeforeEach
    fun setup() {
        // Author 데이터 삽입
        val authors = AuthorTable.batchInsert((1..10)) { i ->
            this[AuthorTable.name] = "Author $i"
            this[AuthorTable.birthDate] = LocalDate.of(1970 + i, 1, 1).toKotlinLocalDate()
        }.map { AuthorEntity.wrapRow(it) }

        // Publisher 데이터 삽입
        val publishers = PublisherTable.batchInsert((1..10)) { i ->
            this[PublisherTable.name] = "Publisher $i"
            this[PublisherTable.country] = if (i % 2 == 0) "Country A" else "Country B"
        }.map { PublisherEntity.wrapRow(it) }

        // Book 데이터 삽입
        BookTable.batchInsert((1..10)) { i ->
            this[BookTable.title] = "Book $i"
            this[BookTable.authorId] = authors[(i - 1) % authors.size].id
            this[BookTable.publisherId] = publishers[(i - 1) % publishers.size].id
            this[BookTable.publishedYear] = 2000 + i
        }

        // ID 3인 출판사의 책 데이터 삽입
        BookTable.batchInsert((11..15)) { i ->
            this[BookTable.title] = "Book $i"
            this[BookTable.authorId] = authors[(i - 1) % authors.size].id
            this[BookTable.publisherId] = publishers[2].id
            this[BookTable.publishedYear] = 2000 + i
        }
    }
}
