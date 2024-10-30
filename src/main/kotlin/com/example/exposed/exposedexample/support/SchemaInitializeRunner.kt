package com.example.exposed.exposedexample.support

import com.example.exposed.exposedexample.dao.AuthorTable
import com.example.exposed.exposedexample.dao.BookTable
import com.example.exposed.exposedexample.dao.PublisherTable
import com.example.exposed.exposedexample.domain.BookInfoDTO
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
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
//        SchemaUtils.drop(AuthorTable, BookTable, PublisherTable)
//        SchemaUtils.create(AuthorTable, BookTable, PublisherTable)

//        setupData()
//        fetchBookInfos(2000, "%1%").forEach {
//            println(it)
//        }
    }

    // 예제 데이터 삽입
    fun setupData() {
        // Batch Insert
        AuthorTable.batchInsert(listOf("김철수", "홍길동", "김영희")) { authorName ->
            this[AuthorTable.name] = authorName
            this[AuthorTable.birthDate] = LocalDate(1965, 7, 31)
        }

        val publisherId = PublisherTable.insertAndGetId {
            it[name] = "한빛미디어"
            it[country] = "KOREA"
        }

        // Select Author ID
        val authorId = AuthorTable
            .select(AuthorTable.id)
            .where { AuthorTable.name eq "홍길동" }
            .limit(1)
            .first()[AuthorTable.id]

        // Batch Insert
        BookTable.batchInsert((1..10).toList()) { i ->
            this[BookTable.title] = "Book Title $i"
            this[BookTable.authorId] = authorId
            this[BookTable.publisherId] = publisherId
            this[BookTable.publishedYear] = 1997 + i
        }
    }

    // Inner Join, 동적 쿼리, DTO 매핑
    fun fetchBookInfos(year: Int? = null, title: String? = null): List<BookInfoDTO> {
        // Inner Join
        val query = BookTable
//            .join(
//                AuthorEntity,
//                JoinType.INNER,
//                additionalConstraint = { BookEntity.authorId eq AuthorEntity.id }
//            )
            .innerJoin(AuthorTable)
            .innerJoin(PublisherTable)
            .select(BookTable.id, BookTable.title, AuthorTable.name, PublisherTable.name)

        // 동적 쿼리
        query
            .addOrIfNotNull(BookTable.publishedYear eqIfNotNull year)
            .addOrIfNotNull(BookTable.title likeIfNotNull title)

        // DTO 매핑
        return query.map { row: ResultRow ->
            // get = throw exception, getOrNull = null
            println("BookEntity.publishedYear :: ${row.getOrNull(BookTable.publishedYear)}")

            BookInfoDTO(
                bookId = row[BookTable.id].value,
                title = row[BookTable.title],
                authorName = row[AuthorTable.name],
                publisherName = row[PublisherTable.name]
            )
        }
    }

    // index 조회 예시
    fun fetchBookInfosByTitle(title: String): List<BookInfoDTO> {
        return BookTable
            .select(BookTable.id, BookTable.title, BookTable.authorId, BookTable.publisherId)
            .where { BookTable.title eq title }
            .map { row: ResultRow ->
                BookInfoDTO(
                    bookId = row[BookTable.id].value,
                    title = row[BookTable.title],
                    authorName = row[AuthorTable.name],
                    publisherName = row[PublisherTable.name]
                )
            }
    }
}
