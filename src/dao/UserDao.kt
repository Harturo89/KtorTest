package com.itcs.back.dao

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = uuid("id").primaryKey()
    val name = text("name")
    val image = text("image")
    val color = text("color")
}