package com.itcs.back.controller

import com.itcs.back.dao.Users
import com.itcs.back.model.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

class UsersController {

    fun getUsers(): MutableList<User> {

        val users = mutableListOf<User>()

        transaction {
            Users.selectAll().map {
                users.add(
                    User(
                        id = it[Users.id],
                        name = it[Users.name],
                        image = it[Users.image],
                        color = it[Users.color]
                    )
                )
            }
        }
        return users
    }

    fun getUser(id: UUID): User? {
        return transaction {
            Users.select { Users.id eq id }
                .map {
                    User(
                        id = it[Users.id],
                        name = it[Users.name],
                        image = it[Users.image],
                        color = it[Users.color]
                    )
                }
                .firstOrNull()
        }
    }

    fun addUser(candidate: User) {
        transaction {
            Users.insert {
                it[name] = candidate.name
                it[image] = candidate.image
                it[color] = candidate.color
            }
        }
    }

    fun updateUser(candidate: User, id: UUID) {
        transaction {
            Users.update({ Users.id eq id }) {
                it[name] = candidate.name
                it[image] = candidate.image
                it[color] = candidate.color
            }
        }
    }


}