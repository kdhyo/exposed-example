package com.example.exposed.exposedexample.support

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.LikeEscapeOp
import org.jetbrains.exposed.sql.LikePattern
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or

fun Query.addAndIfNotNull(condition: Op<Boolean>?): Query {
    return condition?.let {
        adjustWhere {
            if (this == null) it else this and it
        }
    } ?: this
}

fun Query.addOrIfNotNull(condition: Op<Boolean>?): Query {
    return condition?.let {
        adjustWhere {
            if (this == null) it else this or it
        }
    } ?: this
}

infix fun <T> ExpressionWithColumnType<T>.eqIfNotNull(t: T?): Op<Boolean>? = t?.let { this eq t }

infix fun <T : String?> Expression<T>.likeIfNotNull(pattern: String?): LikeEscapeOp? =
    pattern?.let { this like LikePattern(it) }

infix fun <T : Comparable<T>> ExpressionWithColumnType<T>.greaterEqIfNotNull(t: T?): Op<Boolean>? =
    t?.let { this greaterEq it }

infix fun <T : Comparable<T>> ExpressionWithColumnType<T>.lessEqIfNotNull(t: T?): Op<Boolean>? =
    t?.let { this lessEq it }
