package com.example.exposed.exposedexample.support

import com.example.exposed.exposedexample.dao.AuthorTable
import com.example.exposed.exposedexample.dao.BookTable
import com.example.exposed.exposedexample.dao.PublisherTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class SchemaCleanupTask {
    @EventListener
    fun onContextClosedEvent(event: ContextClosedEvent) {
        println("Schema Cleanup")
        SchemaUtils.drop(AuthorTable, BookTable, PublisherTable)
    }
}
