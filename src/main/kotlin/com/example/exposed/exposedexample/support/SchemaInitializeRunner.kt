package com.example.exposed.exposedexample.support

import com.example.exposed.exposedexample.dao.AuthorEntity
import com.example.exposed.exposedexample.dao.BookEntity
import com.example.exposed.exposedexample.dao.PublisherEntity
import com.example.exposed.exposedexample.domain.BookInfoDTO
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class SchemaInitializeRunner : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println("Schema CleanUp And Initialize")
        SchemaUtils.drop(AuthorEntity, BookEntity, PublisherEntity)
        SchemaUtils.create(AuthorEntity, BookEntity, PublisherEntity)

        setupData()
        fetchBookInfos(2000, "%1%").forEach {
            println(it)
        }
    }

    // 예제 데이터 삽입
    fun setupData() {
        // Batch Insert
        AuthorEntity.batchInsert(listOf("김철수", "홍길동", "김영희")) { authorName ->
            this[AuthorEntity.name] = authorName
            this[AuthorEntity.birthDate] = LocalDate(1965, 7, 31)
        }

        val publisherId = PublisherEntity.insertAndGetId {
            it[name] = "한빛미디어"
            it[country] = "KOREA"
        }

        // Select Author ID
        val authorId = AuthorEntity
            .select(AuthorEntity.id)
            .where { AuthorEntity.name eq "홍길동" }
            .limit(1)
            .first()[AuthorEntity.id]

        // Batch Insert
        BookEntity.batchInsert((1..10).toList()) { i ->
            this[BookEntity.title] = "Book Title $i"
            this[BookEntity.authorId] = authorId
            this[BookEntity.publisherId] = publisherId
            this[BookEntity.publishedYear] = 1997 + i
        }
    }

    // Inner Join, 동적 쿼리, DTO 매핑
    fun fetchBookInfos(year: Int? = null, title: String? = null): List<BookInfoDTO> {
        // Inner Join
        val query = BookEntity
//            .join(
//                AuthorEntity,
//                JoinType.INNER,
//                additionalConstraint = { BookEntity.authorId eq AuthorEntity.id }
//            )
            .innerJoin(AuthorEntity)
            .innerJoin(PublisherEntity)
            .select(BookEntity.id, BookEntity.title, AuthorEntity.name, PublisherEntity.name)

        // 동적 쿼리
        query
            .addOrIfNotNull(BookEntity.publishedYear eqIfNotNull year)
            .addOrIfNotNull(BookEntity.title likeIfNotNull title)

        // DTO 매핑
        return query.map { row: ResultRow ->
            // get = throw exception, getOrNull = null
            println("BookEntity.publishedYear :: ${row.getOrNull(BookEntity.publishedYear)}")

            BookInfoDTO(
                bookId = row[BookEntity.id].value,
                title = row[BookEntity.title],
                authorName = row[AuthorEntity.name],
                publisherName = row[PublisherEntity.name]
            )
        }
    }

    // index 조회 예시
    fun fetchBookInfosByTitle(title: String): List<BookInfoDTO> {
        return BookEntity
            .select(BookEntity.id, BookEntity.title, BookEntity.authorId, BookEntity.publisherId)
            .where { BookEntity.title eq title }
            .map { row: ResultRow ->
                BookInfoDTO(
                    bookId = row[BookEntity.id].value,
                    title = row[BookEntity.title],
                    authorName = row[AuthorEntity.name],
                    publisherName = row[PublisherEntity.name]
                )
            }
    }
}
