package com.example.exposed.exposedexample.dao.entity

import com.example.exposed.exposedexample.dao.AuthorTable
import com.example.exposed.exposedexample.dao.BookTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AuthorEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AuthorEntity>(AuthorTable)

    var name by AuthorTable.name
    var birthDate by AuthorTable.birthDate

    val books by BookEntity referrersOn BookTable.authorId
}
